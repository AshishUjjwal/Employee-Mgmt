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
// @EnableWebSecurity - It switches off the default Spring Boot auto-configuration for security and tells Spring to use your custom security configuration instead.
// @EnableWebSecurity - Creates a springSecurityFilterChain: It automatically creates a bean called springSecurityFilterChain. This is a vital component in Spring Security that intercepts all incoming HTTP requests and applies the security rules you've defined (like checking if a user is authenticated, if they have the right roles, etc.).
public class SecurityConfig {

    /**
     * Tells Spring Security to use our CustomUserDetailsService to load user data from the DB.
     */
    @Bean  // @Bean - This annotation tells the Spring framework: "Run this method and register whatever it returns as a component (a 'bean') in the application context so it can be used anywhere else in the application."
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

    // 1. @Bean public SecurityFilterChain securityFilterChain(HttpSecurity http)
    // This tells Spring Security: "Hey, ignore your default locked-down security rules. Use this set of rules I'm giving you right here."

    // 2. http.csrf(csrf -> csrf.disable()) 
    // CSRF (Cross-Site Request Forgery) is a type of security attack that tricks a user's browser into executing unwanted actions.
    // By default, Spring Security turns CSRF protection on, which requires a special token for POST/PUT/DELETE requests.
    // Why disable it? Because you are building a REST API. REST APIs are generally stateless and use JWTs (JSON Web Tokens) for security instead of browser session cookies. CSRF attacks rely on browser cookies, so if you aren't using session cookies, you don't need CSRF protection.

    // 3. .requestMatchers("/auth/register", "/auth/token", "/auth/validate").permitAll()
    // This is your VIP list. It tells the bouncer: "If someone tries to access these three specific URLs, let them in without asking for an ID or password."

    // /auth/register: A new user needs to be able to sign up before they have an account to log in with.
    // /auth/token: A user needs to be able to submit their username/password to get their JWT token.
    // /auth/validate: Another microservice might need to validate a token quickly.

    // 4. .anyRequest().authenticated()
    // This is the rule for everyone else. It says: "For any other URL in this application (now or in the future), the user MUST be authenticated (logged in / have a valid token) to access it."

    // 5. return http.build();
    // This simply wraps all the rules you just defined into a final SecurityFilterChain object and hands it back to Spring Security to enforce.

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

    // This code snippet is a core part of configuring Spring Security in a Java application. It defines how your application will verify a user's identity (authentication) when they try to log in.

    // 1. @Bean public AuthenticationProvider authenticationProvider() :- Spring Security will automatically discover this AuthenticationProvider and use it whenever a user tries to authenticate.
    // @Bean - tells Spring: "Hey, I'm creating a helper object called an AuthenticationProvider. Please manage it for me and make it available for other parts of the application."
    // AuthenticationProvider - This is the engine that actually checks the credentials (like username and password) against a data source. It's the bridge between your web request and your database.

    // 2. DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    // This is the most common type of AuthenticationProvider used in Spring Security for standard username/password authentication.
    // "Dao" stands for Data Access Object. This tells you that this provider is designed to fetch user data from a data source (like a database or a simple in-memory list).

    // 3. authenticationProvider.setUserDetailsService(userDetailsService());
    // This is the crucial connection to your database. You are telling the provider: "When you need to find a user, don't use your default logic. Use the userDetailsSvc bean I created in the previous step."
    // Remember, CustomUserDetailsService is the class that knows how to fetch a User from your PostgreSQL database by username.

    // 4. authenticationProvider.setPasswordEncoder(passwordEncoder());
    // This is the crucial connection to your password checker. You are telling the provider: "When a user tries to log in, take the password they typed in and compare it to the password stored in the database using this passwordEncoder method."

    // 5. return authenticationProvider;
    // Finally, you return the fully configured provider. Spring Security will now use this specific provider to handle all login requests.


    /**
     * Exposes the AuthenticationManager as a Bean so we can inject it into our AuthController.
     * This manager is what actually triggers the authentication process when a user tries to log in.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
