package com.app.messaging.config;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.app.messaging.domain.User;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Integer getUserId() {
        return user.getId();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Provide authorities or roles here
        return Collections.emptyList(); // Return actual authorities if needed
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implement logic as needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Implement logic as needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implement logic as needed
    }

    @Override
    public boolean isEnabled() {
        return true; // Implement logic as needed
    }
}
