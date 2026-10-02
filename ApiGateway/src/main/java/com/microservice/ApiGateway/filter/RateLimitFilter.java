package com.microservice.ApiGateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global filter that prevents spam by limiting the number of requests 
 * a single IP address can make within a certain time window.
 */
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    // Simple in-memory map to store an IP address and the timestamp of their requests
    private final Map<String, RequestData> clientData = new ConcurrentHashMap<>();
    
    // Limits: Maximum 5 requests allowed per 10 seconds per IP
    private static final int MAX_REQUESTS = 5;
    private static final long TIME_WINDOW_MS = 10000; 

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Get the user's IP address (Safely handling NGINX / Proxies)
        String ipAddress = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        } else {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        
        long currentTime = Instant.now().toEpochMilli();
        
        // Update the counter for this IP address
        clientData.compute(ipAddress, (key, data) -> {
            if (data == null || (currentTime - data.startTime > TIME_WINDOW_MS)) {
                // First request ever, or the 10-second time window has expired, so we reset the counter
                return new RequestData(currentTime, 1);
            } else {
                // Still inside the 10-second window, increment their request count
                data.count++;
                return data;
            }
        });
        
        // Check how many requests they've made
        RequestData data = clientData.get(ipAddress);
        
        if (data.count > MAX_REQUESTS) {
            // Rate limit exceeded! Reject with HTTP 429 Too Many Requests
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete(); // Stop the chain here
        }
        
        // Allowed: Pass the request to the next filter
        return chain.filter(exchange);
    }

    /**
     * Order -3 means it will run first, before Logging (-2) and Authentication (-1).
     * It's good practice to run rate limiting first so we don't waste time authenticating a spammer.
     */
    @Override
    public int getOrder() {
        return -3; 
    }
    
    // Helper class to store request tracking data inside our Map
    private static class RequestData {
        long startTime;
        int count;
        
        RequestData(long startTime, int count) {
            this.startTime = startTime;
            this.count = count;
        }
    }
}
