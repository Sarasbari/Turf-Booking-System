package com.turfbooking.config;

import com.turfbooking.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Custom user principal that implements both UserDetails and OAuth2User
 * Allows seamless integration between form-based and OAuth2 authentication
 */
public class CustomUserPrincipal implements UserDetails, OAuth2User {
    
    private User user;
    private Map<String, Object> attributes;
    
    public CustomUserPrincipal(User user) {
        this.user = user;
    }
    
    public CustomUserPrincipal(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }
    
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }
    
    @Override
    public String getUsername() {
        return user.getEmail();
    }
    
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
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    @Override
    public String getName() {
        return user.getName();
    }
    
    // Getters for user information
    public User getUser() {
        return user;
    }
    
    public Long getUserId() {
        return user.getId();
    }
    
    public String getEmail() {
        return user.getEmail();
    }
    
    public String getDisplayName() {
        return user.getName();
    }
    
    public String getProfilePicture() {
        return user.getProfilePicture();
    }
    
    public User.Role getRole() {
        return user.getRole();
    }
    
    public boolean isAdmin() {
        return user.getRole() == User.Role.ADMIN;
    }
}
