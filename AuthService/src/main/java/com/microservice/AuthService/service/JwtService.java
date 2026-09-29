package com.microservice.AuthService.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtService handles the core cryptographic operations for JSON Web Tokens (JWT).
 * It is responsible for creating new tokens, signing them securely, and validating 
 * incoming tokens to ensure they haven't been tampered with.
 */
@Service
public class JwtService {

    // The secret key used to sign the JWT, injected from application.properties
    @Value("${jwt.secret}")
    private String secret;

    // The time (in milliseconds) after which the token expires, from application.properties
    @Value("${jwt.expirationMs}")
    private long expirationMs;

    /**
     * Validates a given JWT token.
     * It uses the secret key to decrypt the signature. If the signature is invalid, 
     * or the token has expired, this method will throw an exception.
     */
    public void validateToken(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }

    /**
     * Public method to generate a token for a specific user.
     */
    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName);
    }

    /**
     * Internal method that actually builds the JWT token string.
     * It sets the "claims" (custom data), the "subject" (the username), the issue date, 
     * the expiration date, and finally signs it using the HMAC SHA-256 algorithm and our secret key.
     */
    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    /**
     * Helper method to convert our Base-64 encoded secret string (from application.properties) 
     * into a cryptographic Key object that the JWT library can use for signing/verifying.
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
