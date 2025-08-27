package com.turfbooking.repository;

import com.turfbooking.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Review entity operations
 * Handles review queries, ratings, and turf feedback
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Find reviews by turf
    List<Review> findByTurfIdOrderByCreatedAtDesc(Long turfId);
    
    Page<Review> findByTurfIdOrderByCreatedAtDesc(Long turfId, Pageable pageable);
    
    // Find reviews by user
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Find review by user and turf (to prevent duplicate reviews)
    Optional<Review> findByUserIdAndTurfId(Long userId, Long turfId);
    
    // Find review by user, turf, and booking
    Optional<Review> findByUserIdAndTurfIdAndBookingId(Long userId, Long turfId, Long bookingId);
    
    // Find reviews by rating
    List<Review> findByRating(Integer rating);
    
    List<Review> findByRatingGreaterThanEqual(Integer rating);
    
    // Calculate average rating for a turf
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.turf.id = :turfId")
    Double calculateAverageRating(@Param("turfId") Long turfId);
    
    // Count reviews for a turf
    @Query("SELECT COUNT(r) FROM Review r WHERE r.turf.id = :turfId")
    Long countReviewsForTurf(@Param("turfId") Long turfId);
    
    // Find top-rated turfs
    @Query("SELECT r.turf.id, AVG(r.rating) as avgRating, COUNT(r) as reviewCount " +
           "FROM Review r GROUP BY r.turf.id " +
           "HAVING COUNT(r) >= :minReviews " +
           "ORDER BY avgRating DESC, reviewCount DESC")
    List<Object[]> findTopRatedTurfs(@Param("minReviews") Long minReviews, Pageable pageable);
    
    // Find recent reviews
    @Query("SELECT r FROM Review r ORDER BY r.createdAt DESC")
    List<Review> findRecentReviews(Pageable pageable);
    
    // Find reviews with text (non-empty reviews)
    @Query("SELECT r FROM Review r WHERE r.reviewText IS NOT NULL AND r.reviewText != '' " +
           "ORDER BY r.createdAt DESC")
    List<Review> findReviewsWithText(Pageable pageable);
    
    // Count reviews by rating
    @Query("SELECT r.rating, COUNT(r) FROM Review r GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> countReviewsByRating();
    
    // Count reviews by rating for a specific turf
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.turf.id = :turfId " +
           "GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> countReviewsByRatingForTurf(@Param("turfId") Long turfId);
    
    // Find reviews for turfs in a specific city
    @Query("SELECT r FROM Review r WHERE r.turf.city = :city ORDER BY r.createdAt DESC")
    List<Review> findReviewsByCity(@Param("city") String city);
    
    // Check if user has reviewed a turf
    @Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.user.id = :userId AND r.turf.id = :turfId")
    boolean hasUserReviewedTurf(@Param("userId") Long userId, @Param("turfId") Long turfId);
}
