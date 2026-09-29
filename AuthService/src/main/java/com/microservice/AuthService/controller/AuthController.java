package com.microservice.AuthService.controller;

import com.microservice.AuthService.dto.AuthRequest;
import com.microservice.AuthService.dto.RegisterRequest;
import com.microservice.AuthService.entity.UserCredential;
import com.microservice.AuthService.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController is the entry point for clients (like your frontend or other services) 
 * to interact with the Authentication system. It provides HTTP endpoints for registration, 
 * login (getting a token), and token validation.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Endpoint: POST /auth/register
     * Purpose: Registers a new user in the system.
     * What happens: It takes the user details from the request body, passes it to the 
     * AuthService to encrypt the password, and saves it in the database.
     */
    @PostMapping("/register")
    public String addNewUser(@RequestBody RegisterRequest request) {
        return service.saveUser(request);
    }

    /**
     * Endpoint: POST /auth/token
     * Purpose: Authenticates a user and returns a JWT token if successful.
     * What happens: 
     * 1. Takes username & password via AuthRequest.
     * 2. Uses Spring Security's AuthenticationManager to verify credentials against the DB.
     * 3. If valid, asks AuthService to generate and return a JWT token.
     */
    @PostMapping("/token")
    public String getToken(@RequestBody AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        if (authenticate.isAuthenticated()) {
            return service.generateToken(authRequest.getUsername());
        } else {
            throw new RuntimeException("Invalid access");
        }
    }

    /**
     * Endpoint: GET /auth/validate
     * Purpose: Checks if a provided JWT token is valid and not expired.
     * What happens: Other microservices (like an API Gateway) can call this endpoint 
     * to verify if an incoming token is legitimate.
     */
    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        service.validateToken(token);
        return "Token is valid";
    }
}
