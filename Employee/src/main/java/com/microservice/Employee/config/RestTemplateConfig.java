package com.microservice.Employee.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class to create Spring Beans.
 * Spring Boot scans this class at startup because of @Configuration.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates a RestTemplate tool for making HTTP requests to other microservices.
     * The @LoadBalanced annotation is the "magic" that intercepts requests
     * and asks the Eureka Server to convert logical names (like "ADDRESS") 
     * into actual physical IP addresses and Ports (e.g., 192.168.1.5:8082).
     * It also automatically balances traffic if multiple servers are running.
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
