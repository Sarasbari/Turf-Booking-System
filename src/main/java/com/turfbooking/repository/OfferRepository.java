package com.turfbooking.repository;

import com.turfbooking.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Offer entity operations
 * Handles promotional offers, discounts, and coupon management
 */
@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    
    // Find offer by code
    Optional<Offer> findByOfferCode(String offerCode);
    
    // Find active offers
    @Query("SELECT o FROM Offer o WHERE o.isActive = true AND " +
           "o.validFrom <= :currentDate AND o.validUntil >= :currentDate " +
           "AND (o.totalUsageLimit IS NULL OR o.currentUsage < o.totalUsageLimit)")
    List<Offer> findActiveOffers(@Param("currentDate") LocalDate currentDate);
    
    // Find offers by validity period
    @Query("SELECT o FROM Offer o WHERE o.validFrom <= :date AND o.validUntil >= :date")
    List<Offer> findOffersByDate(@Param("date") LocalDate date);
    
    // Find offers valid for specific turf types
    @Query("SELECT o FROM Offer o WHERE o.isActive = true AND " +
           "o.validFrom <= :currentDate AND o.validUntil >= :currentDate AND " +
           "(o.totalUsageLimit IS NULL OR o.currentUsage < o.totalUsageLimit) AND " +
           "(o.applicableTurfTypes IS NULL OR o.applicableTurfTypes LIKE CONCAT('%\"', :turfType, '\"%'))")
    List<Offer> findOffersForTurfType(@Param("turfType") String turfType, 
                                     @Param("currentDate") LocalDate currentDate);
    
    // Find expiring offers (expiring within specified days)
    @Query("SELECT o FROM Offer o WHERE o.isActive = true AND " +
           "o.validUntil BETWEEN :currentDate AND :endDate")
    List<Offer> findExpiringOffers(@Param("currentDate") LocalDate currentDate, 
                                  @Param("endDate") LocalDate endDate);
    
    // Find offers by discount type
    @Query("SELECT o FROM Offer o WHERE o.isActive = true AND " +
           "(:isPercentage = true AND o.discountPercentage IS NOT NULL) OR " +
           "(:isPercentage = false AND o.discountAmount IS NOT NULL)")
    List<Offer> findOffersByDiscountType(@Param("isPercentage") boolean isPercentage);
    
    // Find high-value offers (above certain discount threshold)
    @Query("SELECT o FROM Offer o WHERE o.isActive = true AND " +
           "((o.discountPercentage IS NOT NULL AND o.discountPercentage >= :minPercentage) OR " +
           "(o.discountAmount IS NOT NULL AND o.discountAmount >= :minAmount))")
    List<Offer> findHighValueOffers(@Param("minPercentage") java.math.BigDecimal minPercentage,
                                   @Param("minAmount") java.math.BigDecimal minAmount);
    
    // Find popular offers (by usage count)
    @Query("SELECT o FROM Offer o WHERE o.currentUsage > 0 ORDER BY o.currentUsage DESC")
    List<Offer> findPopularOffers(org.springframework.data.domain.Pageable pageable);
    
    // Count offers by status
    @Query("SELECT o.isActive, COUNT(o) FROM Offer o GROUP BY o.isActive")
    List<Object[]> countOffersByStatus();
    
    // Find unused offers (zero usage)
    @Query("SELECT o FROM Offer o WHERE o.currentUsage = 0 AND o.isActive = true")
    List<Offer> findUnusedOffers();
    
    // Find offers with usage limit
    @Query("SELECT o FROM Offer o WHERE o.totalUsageLimit IS NOT NULL AND " +
           "o.currentUsage >= o.totalUsageLimit")
    List<Offer> findFullyUsedOffers();
}
