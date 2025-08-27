package com.turfbooking.service;

import com.turfbooking.model.Offer;
import com.turfbooking.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Offer entity operations
 * Handles promotional offers, discounts, and coupon management
 */
@Service
@Transactional
public class OfferService {
    
    @Autowired
    private OfferRepository offerRepository;
    
    /**
     * Get all offers
     */
    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }
    
    /**
     * Get offer by ID
     */
    public Optional<Offer> getOfferById(Long id) {
        return offerRepository.findById(id);
    }
    
    /**
     * Get offer by code
     */
    public Optional<Offer> getOfferByCode(String offerCode) {
        return offerRepository.findByOfferCode(offerCode);
    }
    
    /**
     * Get active offers
     */
    public List<Offer> getActiveOffers() {
        return offerRepository.findActiveOffers(LocalDate.now());
    }
    
    /**
     * Get offers for specific turf type
     */
    public List<Offer> getOffersForTurfType(String turfType) {
        return offerRepository.findOffersForTurfType(turfType, LocalDate.now());
    }
    
    /**
     * Get expiring offers
     */
    public List<Offer> getExpiringOffers(int days) {
        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = currentDate.plusDays(days);
        return offerRepository.findExpiringOffers(currentDate, endDate);
    }
    
    /**
     * Create new offer
     */
    public Offer createOffer(Offer offer) {
        // Validate offer data
        if (offer.getValidFrom().isAfter(offer.getValidUntil())) {
            throw new RuntimeException("Valid from date cannot be after valid until date");
        }
        
        if (offer.getDiscountPercentage() == null && offer.getDiscountAmount() == null) {
            throw new RuntimeException("Either discount percentage or discount amount must be specified");
        }
        
        if (offer.getDiscountPercentage() != null && 
            (offer.getDiscountPercentage().compareTo(BigDecimal.ZERO) <= 0 || 
             offer.getDiscountPercentage().compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new RuntimeException("Discount percentage must be between 0 and 100");
        }
        
        if (offer.getDiscountAmount() != null && 
            offer.getDiscountAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Discount amount must be greater than 0");
        }
        
        // Check if offer code already exists
        if (offer.getOfferCode() != null && 
            offerRepository.findByOfferCode(offer.getOfferCode()).isPresent()) {
            throw new RuntimeException("Offer code already exists: " + offer.getOfferCode());
        }
        
        return offerRepository.save(offer);
    }
    
    /**
     * Update offer
     */
    public Offer updateOffer(Long id, Offer offerDetails) {
        Offer offer = offerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Offer not found with id: " + id));
        
        // Update fields
        offer.setTitle(offerDetails.getTitle());
        offer.setDescription(offerDetails.getDescription());
        offer.setDiscountPercentage(offerDetails.getDiscountPercentage());
        offer.setDiscountAmount(offerDetails.getDiscountAmount());
        offer.setMinBookingAmount(offerDetails.getMinBookingAmount());
        offer.setValidFrom(offerDetails.getValidFrom());
        offer.setValidUntil(offerDetails.getValidUntil());
        offer.setMaxUsagePerUser(offerDetails.getMaxUsagePerUser());
        offer.setTotalUsageLimit(offerDetails.getTotalUsageLimit());
        offer.setIsActive(offerDetails.getIsActive());
        offer.setApplicableTurfTypes(offerDetails.getApplicableTurfTypes());
        
        return offerRepository.save(offer);
    }
    
    /**
     * Activate/Deactivate offer
     */
    public Offer toggleOfferStatus(Long id) {
        Offer offer = offerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Offer not found with id: " + id));
        
        offer.setIsActive(!offer.getIsActive());
        return offerRepository.save(offer);
    }
    
    /**
     * Delete offer
     */
    public void deleteOffer(Long id) {
        if (!offerRepository.existsById(id)) {
            throw new RuntimeException("Offer not found with id: " + id);
        }
        offerRepository.deleteById(id);
    }
    
    /**
     * Apply offer to booking amount
     */
    public BigDecimal applyOffer(String offerCode, BigDecimal bookingAmount, String turfType) {
        Optional<Offer> offerOpt = offerRepository.findByOfferCode(offerCode);
        
        if (!offerOpt.isPresent()) {
            throw new RuntimeException("Invalid offer code: " + offerCode);
        }
        
        Offer offer = offerOpt.get();
        
        if (!offer.isValid()) {
            throw new RuntimeException("Offer is not valid or has expired");
        }
        
        // Check if offer is applicable to turf type
        if (offer.getApplicableTurfTypes() != null && 
            !offer.getApplicableTurfTypes().contains(turfType)) {
            throw new RuntimeException("Offer is not applicable to this turf type");
        }
        
        // Check minimum booking amount
        if (offer.getMinBookingAmount() != null && 
            bookingAmount.compareTo(offer.getMinBookingAmount()) < 0) {
            throw new RuntimeException("Minimum booking amount not met for this offer");
        }
        
        BigDecimal discountAmount = offer.calculateDiscount(bookingAmount);
        BigDecimal finalAmount = bookingAmount.subtract(discountAmount);
        
        // Ensure final amount is not negative
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }
        
        return finalAmount;
    }
    
    /**
     * Calculate discount amount for offer
     */
    public BigDecimal calculateDiscountAmount(String offerCode, BigDecimal bookingAmount) {
        Optional<Offer> offerOpt = offerRepository.findByOfferCode(offerCode);
        
        if (!offerOpt.isPresent()) {
            return BigDecimal.ZERO;
        }
        
        Offer offer = offerOpt.get();
        return offer.calculateDiscount(bookingAmount);
    }
    
    /**
     * Use offer (increment usage count)
     */
    @Transactional
    public void useOffer(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
            .orElseThrow(() -> new RuntimeException("Offer not found with id: " + offerId));
        
        offer.incrementUsage();
        offerRepository.save(offer);
    }
    
    /**
     * Get offers by discount type
     */
    public List<Offer> getOffersByDiscountType(boolean isPercentage) {
        return offerRepository.findOffersByDiscountType(isPercentage);
    }
    
    /**
     * Get high-value offers
     */
    public List<Offer> getHighValueOffers(BigDecimal minPercentage, BigDecimal minAmount) {
        return offerRepository.findHighValueOffers(minPercentage, minAmount);
    }
    
    /**
     * Get popular offers
     */
    public List<Offer> getPopularOffers(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return offerRepository.findPopularOffers(pageable);
    }
    
    /**
     * Get unused offers
     */
    public List<Offer> getUnusedOffers() {
        return offerRepository.findUnusedOffers();
    }
    
    /**
     * Get fully used offers
     */
    public List<Offer> getFullyUsedOffers() {
        return offerRepository.findFullyUsedOffers();
    }
    
    /**
     * Get offer statistics
     */
    public List<Object[]> getOfferStatsByStatus() {
        return offerRepository.countOffersByStatus();
    }
    
    /**
     * Validate offer code
     */
    public boolean validateOfferCode(String offerCode, BigDecimal bookingAmount, String turfType) {
        try {
            applyOffer(offerCode, bookingAmount, turfType);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
