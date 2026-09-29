package com.microservice.AuthService.service;

import com.microservice.AuthService.dto.RegisterRequest;
import com.microservice.AuthService.entity.UserCredential;
import com.microservice.AuthService.repository.UserCredentialRepository;
import com.microservice.AuthService.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthService contains the business logic for user registration and interacts with JwtService 
 * to handle token generation and validation. It acts as an intermediary layer between the 
 * Controller and the Repository/JwtService.
 */
@Service
public class AuthService {

    @Autowired
    private UserCredentialRepository repository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;

    /**
     * Saves a new user to the database.
     * Crucially, it takes the plain-text password from the user, hashes/encrypts it 
     * using the PasswordEncoder (BCrypt), and saves the encrypted version to the DB.
     */
    public String saveUser(RegisterRequest request) {
        UserCredential credential = AppUtils.dtoToEntity(request);
        credential.setPassword(passwordEncoder.encode(credential.getPassword()));
        repository.save(credential);
        return "User added to the system";
    }

    /**
     * Delegates the token generation process to the JwtService.
     */
    public String generateToken(String username) {
        return jwtService.generateToken(username);
    }

    /**
     * Delegates the token validation process to the JwtService.
     */
    public void validateToken(String token) {
        jwtService.validateToken(token);
    }
}
