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
        // Jwts.parserBuilder()
        // This is the starting point for creating or parsing a JWT. It provides a builder object that allows you to configure various aspects of the JWT before building or parsing it.

        // setSigningKey(getSignKey())
        // This sets the signing key, which is used to verify the authenticity of the JWT. 
        // In this case, it uses the `getSignKey()` method to retrieve the signing key (likely a secret key or public key depending on the signing algorithm).

        // .build()
        // This finalizes the configuration and creates an instance of `JwtParser`, which is used to parse and validate JWTs.

        // .parseClaimsJws(token)
        // This is the core method that performs the actual parsing and validation of the JWT. It attempts to parse the given `token` and verifies its signature using the configured signing key. 
        // If the token is valid and its signature is verified, it returns a `Jws<Claims>` object containing the claims (the payload) of the token. If the token is invalid (e.g., expired, tampered with, or uses an incorrect signature), it will throw an exception.
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
        // Jwts.builder()
        // This initializes the JWT builder, which is the starting point for creating a new JWT.

        // setClaims(claims)
        // This adds any custom data or "claims" you want to include in the token. 
        // Claims are essentially key-value pairs that provide additional information about the token, such as the user's role or other metadata.

        // setSubject(userName)
        // This sets the "subject" of the token. In JWT terminology, the subject is typically the user ID or username of the person for whom the token is being issued.

        // setIssuedAt(new Date(System.currentTimeMillis()))
        // This sets the "issued at" time, which is the timestamp indicating when the token was created.

        // setExpiration(new Date(System.currentTimeMillis() + expirationMs))
        // This sets the "expiration time" of the token. It calculates the expiration timestamp by adding the `expirationMs` (which is typically defined in your application properties) to the current time.

        // signWith(getSignKey(), SignatureAlgorithm.HS256)
        // This is a crucial step where the token is digitally signed. It uses a cryptographic key (obtained from `getSignKey()`) and the HMAC SHA-256 algorithm to sign the token.

        // compact()
        // This finalizes the JWT and returns it as a compact, URL-safe string.
        
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

        // byte[] keyBytes = Decoders.BASE64.decode(secret);
        // Decoders.BASE64.decode(secret): This is taking your secret key (which is a String in your `secret` variable, likely stored in base64 format in `application.properties`) and decoding it from base64 into a raw byte array. Base64 is a way to represent binary data as text, making it safe to store in configuration files.

        // return Keys.hmacShaKeyFor(keyBytes);
        // Keys.hmacShaKeyFor(keyBytes): This is a static utility method from the JJWT library (Java JWT). It takes the decoded byte array (`keyBytes`) and uses it to create a cryptographic `Key` object. Specifically, it creates an HMAC SHA-256 signing key, which is a secret key used for both signing and verifying JWTs using the HMAC SHA-256 algorithm.
    }
}
