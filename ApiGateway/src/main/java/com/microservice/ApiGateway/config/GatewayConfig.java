package com.microservice.ApiGateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class to define Spring beans required by the API Gateway.
 */
@Configuration
public class GatewayConfig {

    /**
     * Creates a WebClient.Builder bean to be managed by the Spring context.
     * WebClient is the non-blocking, reactive alternative to RestTemplate used for making HTTP requests.
     * 
     * @LoadBalanced annotation is crucial here. It tells Spring Cloud that any request made using 
     * this WebClient should be intercepted by the LoadBalancer. 
     * Because of this, we can use logical service names (like http://AUTHSERVICE) in our requests 
     * instead of hardcoded IPs/Ports, and Eureka will resolve the actual location.
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
