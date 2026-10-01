package com.microservice.AuthService.config;

import com.microservice.AuthService.entity.UserCredential;
import com.microservice.AuthService.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * CustomUserDetailsService implements Spring Security's UserDetailsService interface.
 * Spring Security needs a way to fetch user data (like passwords and roles) from the database 
 * during the login process. This class provides that bridge.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserCredentialRepository repository;

    /**
     * This method is called automatically by Spring Security's AuthenticationManager when 
     * it attempts to authenticate a user.
     * 
     * @param username The username provided by the person trying to log in.
     * @return UserDetails A Spring Security object containing the user's data (CustomUserDetails).
     * @throws UsernameNotFoundException If the user doesn't exist in the database.
     */

    // This is the method that Spring Security calls to get user information when someone tries to log in.

    // public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // username: The identifier (email or username) the user typed into the login form.

    // @throws UsernameNotFoundException: This is an exception that gets thrown if the username is not found in the database. It's a way to tell Spring Security, "This user doesn't exist, so deny the login attempt."

    // throws UsernameNotFoundException: This is a Java language feature called "exception declaration." It's like putting up a warning sign that says, "Be careful! When you call this method, it might fail with a UsernameNotFoundException, and you should be prepared to handle it (or let it be handled by Spring Security)."

    // In this specific case, you are intentionally throwing this exception if the user is not found:
    // .orElseThrow(() -> new UsernameNotFoundException("user not found with name :" + username));

    // So, the "throws" keyword here is essential because you are explicitly telling the Java compiler that this method is allowed to throw that specific error.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find the user entity from the database
        Optional<UserCredential> credential = repository.findByUsername(username);
        
        // Convert our UserCredential entity into a CustomUserDetails object that Spring Security understands.
        return credential.map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("user not found with name :" + username));
    }
}
