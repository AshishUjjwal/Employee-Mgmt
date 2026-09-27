package com.microservice.Employee.exception; // Declares the package this class belongs to

public class ResourceAlreadyExistsException extends RuntimeException { // Extends RuntimeException to create an unchecked exception
    
    public ResourceAlreadyExistsException(String message) { // Constructor that accepts an error message
        super(message); // Passes the error message to the parent RuntimeException class
    }
}
