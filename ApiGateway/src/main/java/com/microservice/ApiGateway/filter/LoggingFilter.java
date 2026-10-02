package com.microservice.ApiGateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global filter that logs the IP address and requested path for every incoming request.
 */
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    // Logger to print messages to the console
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Get the path the user is trying to visit (e.g., /employee/all)
        String path = exchange.getRequest().getURI().getPath();
        
        // Check for NGINX X-Forwarded-For header first to get the real client IP
        String ipAddress = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            // Fallback if no proxy is used
            ipAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        } else {
            // X-Forwarded-For can contain multiple IPs if there are multiple proxies. 
            // The first one is the original client.
            ipAddress = ipAddress.split(",")[0].trim();
        }
        // Log the information
        logger.info("Security Log - Incoming request from IP: {} for path: {}", ipAddress, path);
        
        // Pass the request to the next filter in the chain
        return chain.filter(exchange);
    }

    /**
     * Order -2 means it will run before the AuthenticationFilter (which is -1).
     * The lower the number, the earlier it runs.
     */
    @Override
    public int getOrder() {
        return -2; 
    }

    // Your API Gateway comes with a bunch of built-in, invisible filters provided automatically by Spring (for example, the filter that routes the traffic, or the filter that handles WebSockets).
    // Most of those built-in Spring filters have an order starting at 0, 1, 2, 100, etc.
    
    //By using negative numbers (-3, -2, -1), we are effectively telling Spring:
    //"I don't care what default filters you have. Put my custom security filters at the very, very front of the line!"
}




// The API Gateway needs the true client IP for a few very critical reasons, mostly related to security and monitoring:

// 1. Accurate Rate Limiting
// Imagine 1,000 different users are accessing your app through NGINX at the same time.

// If the Gateway only sees NGINX's IP, it thinks one single person is making 1,000 requests. Your Rate Limiter will immediately block NGINX, which accidentally brings down your entire application for everyone.
// If the Gateway sees the true client IP, it can track each user individually. If user A spams the server, only user A gets blocked, and the other 999 users are unaffected.
// 2. Security Audits & Tracking Hackers
// If someone tries to hack your application or exploit an endpoint, you need to look at your logs to find out who did it.

// If your LoggingFilter just logs NGINX's IP for every single request, your logs are useless. You won't know where the attack came from.
// By logging the true client IP, you can trace malicious activity back to a specific person or country and block them permanently at the firewall level.
// 3. Geo-Blocking / Analytics
// Sometimes you want to know where your users are coming from (e.g., 50% from the US, 30% from India, etc.) or you legally need to block traffic from certain countries. You can only do this if the API Gateway has access to the true IP address of the user who clicked the button on their browser!