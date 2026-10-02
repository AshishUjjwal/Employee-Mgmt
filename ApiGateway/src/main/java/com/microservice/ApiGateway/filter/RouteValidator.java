package com.microservice.ApiGateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

/**
 * Utility class to determine whether a given request route requires authentication or not.
 */
@Component
public class RouteValidator {

    /**
     * A list of API endpoints that are open to the public. 
     * These endpoints do NOT require a JWT token.
     * Examples: 
     * - /auth/register: Anyone can create an account
     * - /auth/token: Anyone can login to get a token
     * - /eureka: Eureka dashboard and registration endpoints
     */
    public static final List<String> openApiEndpoints = List.of(
            "/auth/register",
            "/auth/token",
            "/eureka"
    );

    /**
     * A functional predicate that takes a ServerHttpRequest and returns true if the route is secured.
     * Logic: 
     * It streams through the 'openApiEndpoints' list.
     * noneMatch() returns true ONLY IF none of the open endpoints are part of the current request URL.
     * In other words: If it's NOT an open endpoint, it IS secured (returns true).
     */
    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
