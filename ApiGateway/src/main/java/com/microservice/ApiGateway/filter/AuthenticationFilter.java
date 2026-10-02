package com.microservice.ApiGateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global filter that intercepts all incoming requests to the API Gateway.
 * Its purpose is to check for a valid JWT token in the Authorization header 
 * before forwarding requests to protected microservices (like Employee or Address).
 */
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    // Helper component to check if the current request route requires authentication
    @Autowired
    private RouteValidator validator;

    // WebClient builder to make non-blocking REST calls to other services (like AuthService)
    @Autowired
    private WebClient.Builder webClientBuilder;

    /**
     * The core method that runs for every incoming request.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Together: Mono<Void> means "I am returning a promise that a task will finish in the background, but when it's done, there is no data to hand back." This is how WebFlux knows the filter has completely finished its job.
        // Mono<Void>: A background promise that returns nothing when finished. It tells the gateway when the asynchronous filter task is fully complete.
        // ServerWebExchange exchange: A container holding both the incoming HTTP Request (to read headers/URLs) and the outgoing HTTP Response (to send errors like 401).
        // GatewayFilterChain chain: The remaining list of filters. You call chain.filter(exchange) to approve the request and pass it to the next step.

        // Step 1: Check if the request path is one of the secured endpoints
        if (validator.isSecured.test(exchange.getRequest())) {
            
            // Step 2: Ensure the request has an 'Authorization' header
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                // If missing, reject the request with HTTP 401 Unauthorized
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Step 3: Extract the Authorization header value
            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            
            // Step 4: Check if the token has the "Bearer " prefix, and if so, remove it to get just the JWT string
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            }

            // Step 5: Make a REST call to the AUTH-SERVICE to validate the token
            // Using WebClient because API Gateway uses reactive programming (WebFlux) 
            // and we cannot use blocking calls like RestTemplate.
            return webClientBuilder.build()
                    .get() // HTTP GET request
                    .uri("http://AUTHSERVICE/auth/validate?token=" + authHeader) // URL registered in Eureka
                    .retrieve()
                    .bodyToMono(String.class) // Expect a String response from AuthService
                    .flatMap(response -> {
                        // If we reach here, the token is valid (AuthService returned success).
                        // Step 6: Let the request proceed to its destination service.
                        return chain.filter(exchange);
                    })
                    .onErrorResume(e -> {
                        // If AuthService threw an error (e.g., Token expired, invalid signature)
                        // Reject the request with HTTP 401 Unauthorized
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    });
        }
        
        // If the route is not secured (e.g., /auth/register or /auth/token), just let it pass through
        return chain.filter(exchange);
    }

    /**
     * Determines the order in which this filter is executed relative to other global filters.
     * Returning a negative number ensures this runs very early in the filter chain.
     */
    @Override
    public int getOrder() {
        return -1;
    }
}


// What if hacker .. use multiple ip ... and spam ... ??

// That is a brilliant question! What you just described is called a DDoS attack (Distributed Denial of Service).

// If a hacker uses a botnet (thousands of infected computers with different IP addresses) to spam your server, our simple IP-based RateLimitFilter will fail to stop them.

// Because every request comes from a new IP address, our filter thinks, "Oh, this is just a normal user making their first request," and lets them through. Eventually, your server gets overwhelmed and crashes.

// To protect against this, big companies use a layered defense strategy. Here is what you would do to stop them:

// 1. Token-Based Rate Limiting (Application Level)
// Instead of counting requests per IP address, you count requests per Logged-in User.

// In your AuthenticationFilter, once you extract the JWT, you can check the userId.
// You tell the rate limiter: "Limit this User ID to 50 requests per minute."
// Even if the hacker changes their IP 1,000 times, they are still using the same JWT token, so they get blocked instantly!
// (Note: This doesn't protect open endpoints like /auth/register since they don't require a token).
// 2. Web Application Firewall (WAF) / Cloudflare (Infrastructure Level)
// You don't usually fight DDoS attacks inside Spring Boot. Instead, you put a massive shield like Cloudflare or AWS WAF in front of your NGINX server.

// Cloudflare sits between the hacker and your server. It analyzes the behavior of the traffic using AI.
// If it sees 10,000 different IPs suddenly rushing to your site, it will automatically throw up a CAPTCHA page ("Click the bicycles to prove you are human"). Bots can't solve it, so the spam never even reaches your NGINX or Spring Gateway.
// 3. Device Fingerprinting
// Even if the hacker changes their IP address, their browser and device leave a unique signature (screen size, OS version, installed fonts, etc.). Advanced security systems track this "fingerprint." If 1,000 requests come from 1,000 different IPs, but they all have the exact same device fingerprint, the system blocks them.

// Summary for your notes:
// IP-based rate limiting stops casual spammers and broken scripts. To stop a real hacker with multiple IPs (DDoS), you must use Token-based rate limiting (JWT) or a cloud shield like Cloudflare.