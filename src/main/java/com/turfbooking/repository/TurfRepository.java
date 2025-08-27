package com.turfbooking.repository;

import com.turfbooking.model.Turf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for Turf entity operations
 * Provides custom queries for turf search and filtering
 */
@Repository
public interface TurfRepository extends JpaRepository<Turf, Long> {
    
    // Basic filters
    List<Turf> findByIsActiveTrue();
    
    List<Turf> findByTurfType(Turf.TurfType turfType);
    
    List<Turf> findByCityContainingIgnoreCase(String city);
    
    List<Turf> findByAreaContainingIgnoreCase(String area);
    
    // Active turfs by type
    @Query("SELECT t FROM Turf t WHERE t.isActive = true AND t.turfType = :turfType")
    List<Turf> findActiveTurfsByType(@Param("turfType") Turf.TurfType turfType);
    
    // Search by location (city or area)
    @Query("SELECT t FROM Turf t WHERE t.isActive = true AND " +
           "(LOWER(t.city) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(t.area) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(t.location) LIKE LOWER(CONCAT('%', :location, '%')))")
    List<Turf> findByLocationSearch(@Param("location") String location);
    
    // Price range filter
    @Query("SELECT t FROM Turf t WHERE t.isActive = true AND " +
           "t.pricePerHour BETWEEN :minPrice AND :maxPrice")
    List<Turf> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                               @Param("maxPrice") BigDecimal maxPrice);
    
    // Top rated turfs
    @Query("SELECT t FROM Turf t WHERE t.isActive = true AND t.totalReviews > 0 " +
           "ORDER BY t.rating DESC, t.totalReviews DESC")
    List<Turf> findTopRatedTurfs(Pageable pageable);
    
    // Search with multiple filters
    @Query("SELECT t FROM Turf t WHERE t.isActive = true " +
           "AND (:turfType IS NULL OR t.turfType = :turfType) " +
           "AND (:city IS NULL OR LOWER(t.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
           "AND (:minPrice IS NULL OR t.pricePerHour >= :minPrice) " +
           "AND (:maxPrice IS NULL OR t.pricePerHour <= :maxPrice) " +
           "AND (:minRating IS NULL OR t.rating >= :minRating)")
    Page<Turf> findTurfsWithFilters(@Param("turfType") Turf.TurfType turfType,
                                   @Param("city") String city,
                                   @Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   @Param("minRating") BigDecimal minRating,
                                   Pageable pageable);
    
    // Nearby turfs (within distance range)
    @Query("SELECT t FROM Turf t WHERE t.isActive = true AND " +
           "t.latitude IS NOT NULL AND t.longitude IS NOT NULL AND " +
           "(6371 * acos(cos(radians(:lat)) * cos(radians(t.latitude)) * " +
           "cos(radians(t.longitude) - radians(:lng)) + " +
           "sin(radians(:lat)) * sin(radians(t.latitude)))) <= :distance")
    List<Turf> findNearbyTurfs(@Param("lat") double latitude, 
                              @Param("lng") double longitude, 
                              @Param("distance") double distanceKm);
    
    // Count active turfs by city
    @Query("SELECT t.city, COUNT(t) FROM Turf t WHERE t.isActive = true GROUP BY t.city")
    List<Object[]> countTurfsByCity();
    
    // Update turf rating
    @Modifying
    @Query("UPDATE Turf t SET t.rating = :rating, t.totalReviews = :totalReviews WHERE t.id = :turfId")
    void updateTurfRating(@Param("turfId") Long turfId, 
                         @Param("rating") BigDecimal rating, 
                         @Param("totalReviews") Integer totalReviews);
}