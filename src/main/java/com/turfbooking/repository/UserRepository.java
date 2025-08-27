package com.turfbooking.repository;

import com.turfbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Repository interface for User entity operations
 * Handles user authentication and profile management
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Find user by Google ID
    Optional<User> findByGoogleId(String googleId);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Check if Google ID exists
    boolean existsByGoogleId(String googleId);
    
    // Find users by role
    List<User> findByRole(User.Role role);
    
    // Find users by name (case-insensitive partial match)
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);
    
    // Find user by email or Google ID
    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.googleId = :identifier")
    Optional<User> findByEmailOrGoogleId(@Param("identifier") String identifier);
    
    // Count users by role
    @Query("SELECT u.role, COUNT(u) FROM User u GROUP BY u.role")
    List<Object[]> countUsersByRole();
    
    // Find recent users
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    List<User> findRecentUsers(org.springframework.data.domain.Pageable pageable);
}
