package com.microservice.Employee.exception; // Declares the package this class belongs to

import org.springframework.http.HttpStatus; // Imports HttpStatus enum for standard HTTP status codes (e.g., 404, 500)
import org.springframework.http.ResponseEntity; // Imports ResponseEntity to wrap the entire HTTP response (headers, body, status)
import org.springframework.web.bind.annotation.ExceptionHandler; // Imports annotation to mark methods as exception handlers
import org.springframework.web.bind.annotation.RestControllerAdvice; // Imports annotation to apply global exception handling across all controllers
import org.springframework.web.context.request.WebRequest; // Imports WebRequest to access incoming request details (like URI)

import java.time.LocalDateTime; // Imports LocalDateTime to record the exact time the error occurred
import java.util.HashMap; // Imports HashMap to store response data as key-value pairs
import java.util.Map; // Imports Map interface

@RestControllerAdvice // Tells Spring this class intercepts exceptions thrown by ANY controller and returns JSON
public class GlobalExceptionHandler { // Defines the class for global exception handling

    // Specific Exception Handler
    @ExceptionHandler(ResourceNotFoundException.class) // Tells Spring to invoke this method when ResourceNotFoundException is thrown
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) { // Method signature for handling the exception
        Map<String, Object> body = new HashMap<>(); // Creates a Map to hold the JSON response body
        body.put("timestamp", LocalDateTime.now()); // Adds the current date and time to the response
        body.put("message", ex.getMessage()); // Adds the custom error message (e.g., "Employee not found") to the response
        body.put("status", HttpStatus.NOT_FOUND.value()); // Adds the integer status code (404) to the response
        body.put("path", request.getDescription(false).replace("uri=", "")); // Adds the requested URI path (e.g., /v1/Data/getEmployee/1) to the response
        
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND); // Returns the map as JSON along with the 404 HTTP status code
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // Global / Generic Exception Handler
    @ExceptionHandler(Exception.class) // Tells Spring to invoke this method for ANY other exception (catch-all)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) { // Method signature for handling generic exceptions
        Map<String, Object> body = new HashMap<>(); // Creates a Map to hold the JSON response body
        body.put("timestamp", LocalDateTime.now()); // Adds the current date and time to the response
        body.put("message", "An unexpected error occurred"); // Adds a generic, safe error message to the response
        body.put("details", ex.getMessage()); // Adds the actual system error message details for debugging purposes
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()); // Adds the integer status code (500) to the response
        body.put("path", request.getDescription(false).replace("uri=", "")); // Adds the requested URI path to the response
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR); // Returns the map as JSON along with the 500 HTTP status code
    }
}
