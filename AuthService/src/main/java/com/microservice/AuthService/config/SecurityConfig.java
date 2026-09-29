package com.microservice.AuthService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig is the main configuration class for Spring Security in this microservice.
 * It defines what endpoints are public vs protected, how passwords are hashed, 
 * and wires up the custom user details service to the authentication manager.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Tells Spring Security to use our CustomUserDetailsService to load user data from the DB.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    /**
     * Configures the security filter chain. 
     * - csrf.disable(): Cross-Site Request Forgery is typically disabled for stateless REST APIs.
     * - requestMatchers(...).permitAll(): Allows anyone to access /auth/register, /token, and /validate without a JWT or login.
     * - anyRequest().authenticated(): Every other endpoint in this service (if added later) will require authentication.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register", "/auth/token", "/auth/validate").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    /**
     * Configures the PasswordEncoder. 
     * BCrypt is a strong hashing algorithm used to securely store and verify passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the AuthenticationProvider which acts as the database-backed authentication mechanism.
     * It uses our custom UserDetailsService to find the user, and the BCrypt encoder to verify the password.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * Exposes the AuthenticationManager as a Bean so we can inject it into our AuthController.
     * This manager is what actually triggers the authentication process when a user tries to log in.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
