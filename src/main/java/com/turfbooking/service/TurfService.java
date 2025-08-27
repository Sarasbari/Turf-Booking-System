package com.turfbooking.service;

import com.turfbooking.model.Turf;
import com.turfbooking.repository.TurfRepository;
import com.turfbooking.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Turf entity operations
 * Handles business logic for turf management, search, and ratings
 */
@Service
@Transactional
public class TurfService {
    
    @Autowired
    private TurfRepository turfRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    /**
     * Get all active turfs
     */
    public List<Turf> getAllActiveTurfs() {
        return turfRepository.findByIsActiveTrue();
    }
    
    /**
     * Get turf by ID
     */
    public Optional<Turf> getTurfById(Long id) {
        return turfRepository.findById(id);
    }
    
    /**
     * Get turfs by type
     */
    public List<Turf> getTurfsByType(Turf.TurfType turfType) {
        return turfRepository.findActiveTurfsByType(turfType);
    }
    
    /**
     * Search turfs by location (city, area, or full location)
     */
    public List<Turf> searchTurfsByLocation(String location) {
        return turfRepository.findByLocationSearch(location);
    }
    
    /**
     * Get turfs within price range
     */
    public List<Turf> getTurfsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return turfRepository.findByPriceRange(minPrice, maxPrice);
    }
    
    /**
     * Get top rated turfs
     */
    public List<Turf> getTopRatedTurfs(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return turfRepository.findTopRatedTurfs(pageable);
    }
    
    /**
     * Search turfs with multiple filters
     */
    public Page<Turf> searchTurfsWithFilters(Turf.TurfType turfType, String city, 
                                           BigDecimal minPrice, BigDecimal maxPrice, 
                                           BigDecimal minRating, int page, int size, 
                                           String sortBy, String sortDirection) {
        
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? 
                                 Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        return turfRepository.findTurfsWithFilters(turfType, city, minPrice, maxPrice, minRating, pageable);
    }
    
    /**
     * Find nearby turfs using coordinates
     */
    public List<Turf> findNearbyTurfs(double latitude, double longitude, double radiusKm) {
        return turfRepository.findNearbyTurfs(latitude, longitude, radiusKm);
    }
    
    /**
     * Create new turf
     */
    public Turf createTurf(Turf turf) {
        // Set default values
        if (turf.getRating() == null) {
            turf.setRating(BigDecimal.ZERO);
        }
        if (turf.getTotalReviews() == null) {
            turf.setTotalReviews(0);
        }
        if (turf.getIsActive() == null) {
            turf.setIsActive(true);
        }
        
        return turfRepository.save(turf);
    }
    
    /**
     * Update existing turf
     */
    public Turf updateTurf(Long id, Turf turfDetails) {
        Optional<Turf> optionalTurf = turfRepository.findById(id);
        if (optionalTurf.isPresent()) {
            Turf turf = optionalTurf.get();
            
            // Update fields
            turf.setName(turfDetails.getName());
            turf.setLocation(turfDetails.getLocation());
            turf.setCity(turfDetails.getCity());
            turf.setArea(turfDetails.getArea());
            turf.setTurfType(turfDetails.getTurfType());
            turf.setPricePerHour(turfDetails.getPricePerHour());
            turf.setDescription(turfDetails.getDescription());
            turf.setFacilities(turfDetails.getFacilities());
            turf.setImageUrl(turfDetails.getImageUrl());
            turf.setLatitude(turfDetails.getLatitude());
            turf.setLongitude(turfDetails.getLongitude());
            turf.setContactPhone(turfDetails.getContactPhone());
            turf.setOpeningTime(turfDetails.getOpeningTime());
            turf.setClosingTime(turfDetails.getClosingTime());
            turf.setIsActive(turfDetails.getIsActive());
            
            return turfRepository.save(turf);
        }
        throw new RuntimeException("Turf not found with id: " + id);
    }
    
    /**
     * Delete turf (soft delete by setting inactive)
     */
    public void deleteTurf(Long id) {
        Optional<Turf> optionalTurf = turfRepository.findById(id);
        if (optionalTurf.isPresent()) {
            Turf turf = optionalTurf.get();
            turf.setIsActive(false);
            turfRepository.save(turf);
        } else {
            throw new RuntimeException("Turf not found with id: " + id);
        }
    }
    
    /**
     * Update turf rating based on reviews
     */
    @Transactional
    public void updateTurfRating(Long turfId) {
        Double averageRating = reviewRepository.calculateAverageRating(turfId);
        Long reviewCount = reviewRepository.countReviewsForTurf(turfId);
        
        if (averageRating != null && reviewCount != null) {
            BigDecimal rating = BigDecimal.valueOf(averageRating).setScale(2, RoundingMode.HALF_UP);
            turfRepository.updateTurfRating(turfId, rating, reviewCount.intValue());
        }
    }
    
    /**
     * Get turf statistics by city
     */
    public List<Object[]> getTurfStatsByCity() {
        return turfRepository.countTurfsByCity();
    }
    
    /**
     * Check if turf is available for booking at given time
     */
    public boolean isTurfAvailable(Long turfId) {
        Optional<Turf> turf = turfRepository.findById(turfId);
        return turf.isPresent() && turf.get().getIsActive();
    }
    
    /**
     * Get featured turfs for homepage
     */
    public List<Turf> getFeaturedTurfs() {
        // Get top 6 rated turfs with at least 5 reviews
        Pageable pageable = PageRequest.of(0, 6);
        return turfRepository.findTopRatedTurfs(pageable);
    }
}