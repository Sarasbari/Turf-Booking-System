package com.turfbooking.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Offer entity representing promotional offers and discounts
 * Contains discount details, validity period, and usage limits
 */
@Entity
@Table(name = "offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Offer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;
    
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;
    
    @Column(name = "min_booking_amount", precision = 10, scale = 2)
    private BigDecimal minBookingAmount;
    
    @Column(name = "offer_code", unique = true, length = 50)
    private String offerCode;
    
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;
    
    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;
    
    @Column(name = "max_usage_per_user")
    private Integer maxUsagePerUser = 1;
    
    @Column(name = "total_usage_limit")
    private Integer totalUsageLimit;
    
    @Column(name = "current_usage")
    private Integer currentUsage = 0;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "applicable_turf_types", columnDefinition = "JSON")
    private String applicableTurfTypes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    /**
     * Checks if the offer is currently valid
     */
    public boolean isValid() {
        LocalDate today = LocalDate.now();
        return isActive && 
               !today.isBefore(validFrom) && 
               !today.isAfter(validUntil) &&
               (totalUsageLimit == null || currentUsage < totalUsageLimit);
    }
    
    /**
     * Calculates discount amount for a given booking amount
     */
    public BigDecimal calculateDiscount(BigDecimal bookingAmount) {
        if (!isValid() || bookingAmount == null) {
            return BigDecimal.ZERO;
        }
        
        if (minBookingAmount != null && bookingAmount.compareTo(minBookingAmount) < 0) {
            return BigDecimal.ZERO;
        }
        
        if (discountPercentage != null) {
            return bookingAmount.multiply(discountPercentage)
                               .divide(BigDecimal.valueOf(100));
        } else if (discountAmount != null) {
            return discountAmount;
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Increments usage count
     */
    public void incrementUsage() {
        currentUsage++;
    }
}
