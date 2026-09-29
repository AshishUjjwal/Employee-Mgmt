package com.microservice.AuthService.config;

import com.microservice.AuthService.entity.UserCredential;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * CustomUserDetails acts as an adapter/wrapper around our UserCredential entity.
 * Spring Security doesn't know what a "UserCredential" is. It only understands objects 
 * that implement the "UserDetails" interface. This class fulfills that requirement.
 */
public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;

    /**
     * Constructor that takes our database entity and extracts the information 
     * Spring Security cares about (username and password).
     */
    public CustomUserDetails(UserCredential userCredential) {
        this.username = userCredential.getUsername();
        this.password = userCredential.getPassword();
    }

    /**
     * Returns the authorities (roles/permissions) granted to the user.
     * Currently returning null as we haven't implemented roles (like ADMIN/USER).
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // The following methods allow you to implement account locking, expiration, etc.
    // For now, we simply return true meaning all accounts are active and valid.

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
