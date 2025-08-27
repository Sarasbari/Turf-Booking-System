package com.turfbooking.service;

import com.turfbooking.model.Review;
import com.turfbooking.model.User;
import com.turfbooking.model.Turf;
import com.turfbooking.model.Booking;
import com.turfbooking.repository.ReviewRepository;
import com.turfbooking.repository.UserRepository;
import com.turfbooking.repository.TurfRepository;
import com.turfbooking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Review entity operations
 * Handles review creation, management, and rating calculations
 */
@Service
@Transactional
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TurfRepository turfRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private TurfService turfService;
    
    /**
     * Create new review
     */
    @Transactional
    public Review createReview(Long userId, Long turfId, Long bookingId, 
                             Integer rating, String reviewText) {
        // Validate user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Validate turf
        Turf turf = turfRepository.findById(turfId)
            .orElseThrow(() -> new RuntimeException("Turf not found with id: " + turfId));
        
        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        
        // Check if user has already reviewed this turf
        if (reviewRepository.hasUserReviewedTurf(userId, turfId)) {
            throw new RuntimeException("You have already reviewed this turf");
        }
        
        // Validate booking if provided
        Booking booking = null;
        if (bookingId != null) {
            booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
            
            // Check if booking belongs to user and is completed
            if (!booking.getUser().getId().equals(userId)) {
                throw new RuntimeException("You can only review turfs you have booked");
            }
            
            if (booking.getStatus() != Booking.BookingStatus.COMPLETED) {
                throw new RuntimeException("You can only review completed bookings");
            }
        }
        
        // Create review
        Review review = new Review(user, turf, booking, rating, reviewText);
        review = reviewRepository.save(review);
        
        // Update turf rating
        turfService.updateTurfRating(turfId);
        
        return review;
    }
    
    /**
     * Update existing review
     */
    @Transactional
    public Review updateReview(Long reviewId, Long userId, Integer rating, String reviewText) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));
        
        // Check if user owns this review
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only update your own reviews");
        }
        
        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        
        review.setRating(rating);
        review.setReviewText(reviewText);
        review = reviewRepository.save(review);
        
        // Update turf rating
        turfService.updateTurfRating(review.getTurf().getId());
        
        return review;
    }
    
    /**
     * Delete review
     */
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));
        
        // Check if user owns this review
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own reviews");
        }
        
        Long turfId = review.getTurf().getId();
        reviewRepository.delete(review);
        
        // Update turf rating
        turfService.updateTurfRating(turfId);
    }
    
    /**
     * Get review by ID
     */
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }
    
    /**
     * Get reviews for a turf
     */
    public List<Review> getTurfReviews(Long turfId) {
        return reviewRepository.findByTurfIdOrderByCreatedAtDesc(turfId);
    }
    
    /**
     * Get reviews for a turf with pagination
     */
    public Page<Review> getTurfReviewsWithPagination(Long turfId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findByTurfIdOrderByCreatedAtDesc(turfId, pageable);
    }
    
    /**
     * Get user's reviews
     */
    public List<Review> getUserReviews(Long userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Get reviews by rating
     */
    public List<Review> getReviewsByRating(Integer rating) {
        return reviewRepository.findByRating(rating);
    }
    
    /**
     * Get reviews with minimum rating
     */
    public List<Review> getReviewsWithMinRating(Integer minRating) {
        return reviewRepository.findByRatingGreaterThanEqual(minRating);
    }
    
    /**
     * Calculate average rating for turf
     */
    public Double calculateTurfAverageRating(Long turfId) {
        return reviewRepository.calculateAverageRating(turfId);
    }
    
    /**
     * Count reviews for turf
     */
    public Long countTurfReviews(Long turfId) {
        return reviewRepository.countReviewsForTurf(turfId);
    }
    
    /**
     * Get top-rated turfs
     */
    public List<Object[]> getTopRatedTurfs(Long minReviews, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return reviewRepository.findTopRatedTurfs(minReviews, pageable);
    }
    
    /**
     * Get recent reviews
     */
    public List<Review> getRecentReviews(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return reviewRepository.findRecentReviews(pageable);
    }
    
    /**
     * Get reviews with text content
     */
    public List<Review> getReviewsWithText(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return reviewRepository.findReviewsWithText(pageable);
    }
    
    /**
     * Get review statistics by rating
     */
    public List<Object[]> getReviewStatsByRating() {
        return reviewRepository.countReviewsByRating();
    }
    
    /**
     * Get review statistics by rating for specific turf
     */
    public List<Object[]> getTurfReviewStatsByRating(Long turfId) {
        return reviewRepository.countReviewsByRatingForTurf(turfId);
    }
    
    /**
     * Get reviews by city
     */
    public List<Review> getReviewsByCity(String city) {
        return reviewRepository.findReviewsByCity(city);
    }
    
    /**
     * Check if user has reviewed turf
     */
    public boolean hasUserReviewedTurf(Long userId, Long turfId) {
        return reviewRepository.hasUserReviewedTurf(userId, turfId);
    }
    
    /**
     * Get user's review for specific turf
     */
    public Optional<Review> getUserTurfReview(Long userId, Long turfId) {
        return reviewRepository.findByUserIdAndTurfId(userId, turfId);
    }
}
