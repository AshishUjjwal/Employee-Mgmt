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
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find the user entity from the database
        Optional<UserCredential> credential = repository.findByUsername(username);
        
        // Convert our UserCredential entity into a CustomUserDetails object that Spring Security understands.
        return credential.map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("user not found with name :" + username));
    }
}
