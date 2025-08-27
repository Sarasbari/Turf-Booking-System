package com.turfbooking.service;

import com.turfbooking.model.User;
import com.turfbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for User entity operations
 * Handles user registration, authentication, and profile management
 */
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Find user by Google ID
     */
    public Optional<User> findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }
    
    /**
     * Find user by ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Create new user (regular registration)
     */
    public User createUser(String name, String email, String password, String phone) {
        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User already exists with email: " + email);
        }
        
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setRole(User.Role.USER);
        
        return userRepository.save(user);
    }
    
    /**
     * Create or update user from Google OAuth
     */
    public User createOrUpdateGoogleUser(String name, String email, String googleId, String profilePicture) {
        Optional<User> existingUser = userRepository.findByGoogleId(googleId);
        
        if (existingUser.isPresent()) {
            // Update existing user
            User user = existingUser.get();
            user.setName(name);
            user.setProfilePicture(profilePicture);
            return userRepository.save(user);
        } else {
            // Check if user exists with same email but no Google ID
            Optional<User> emailUser = userRepository.findByEmail(email);
            if (emailUser.isPresent()) {
                // Link Google account to existing user
                User user = emailUser.get();
                user.setGoogleId(googleId);
                user.setProfilePicture(profilePicture);
                return userRepository.save(user);
            } else {
                // Create new user
                User user = new User(name, email, googleId, profilePicture);
                return userRepository.save(user);
            }
        }
    }
    
    /**
     * Update user profile
     */
    public User updateUserProfile(Long userId, String name, String phone, String profilePicture) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (name != null && !name.trim().isEmpty()) {
                user.setName(name);
            }
            if (phone != null) {
                user.setPhone(phone);
            }
            if (profilePicture != null) {
                user.setProfilePicture(profilePicture);
            }
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found with id: " + userId);
    }
    
    /**
     * Change user password
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            // Verify current password
            if (user.getPasswordHash() != null && 
                passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
                user.setPasswordHash(passwordEncoder.encode(newPassword));
                userRepository.save(user);
            } else {
                throw new RuntimeException("Current password is incorrect");
            }
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }
    
    /**
     * Verify user password
     */
    public boolean verifyPassword(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPasswordHash() != null) {
            return passwordEncoder.matches(password, user.get().getPasswordHash());
        }
        return false;
    }
    
    /**
     * Get all users (admin only)
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get users by role
     */
    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * Search users by name
     */
    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Get recent users
     */
    public List<User> getRecentUsers(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return userRepository.findRecentUsers(pageable);
    }
    
    /**
     * Get user statistics
     */
    public List<Object[]> getUserStatsByRole() {
        return userRepository.countUsersByRole();
    }
    
    /**
     * Check if user is admin
     */
    public boolean isAdmin(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.isPresent() && user.get().getRole() == User.Role.ADMIN;
    }
    
    /**
     * Promote user to admin
     */
    public void promoteToAdmin(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setRole(User.Role.ADMIN);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }
    
    /**
     * Delete user (admin only)
     */
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }
    
    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Find user by email or Google ID
     */
    public Optional<User> findByEmailOrGoogleId(String identifier) {
        return userRepository.findByEmailOrGoogleId(identifier);
    }
}
