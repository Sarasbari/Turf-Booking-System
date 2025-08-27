package com.turfbooking.controller;

import com.turfbooking.model.Turf;
import com.turfbooking.service.TurfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for Turf operations
 * Provides endpoints for turf management, search, and filtering
 */
@RestController
@RequestMapping("/api/turfs")
@CrossOrigin(origins = "*")
public class TurfController {
    
    @Autowired
    private TurfService turfService;
    
    /**
     * Get all active turfs
     */
    @GetMapping
    public ResponseEntity<List<Turf>> getAllActiveTurfs() {
        List<Turf> turfs = turfService.getAllActiveTurfs();
        return ResponseEntity.ok(turfs);
    }
    
    /**
     * Get turf by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Turf> getTurfById(@PathVariable Long id) {
        Optional<Turf> turf = turfService.getTurfById(id);
        return turf.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get turf details for public view
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<Turf> getTurfDetails(@PathVariable Long id) {
        Optional<Turf> turf = turfService.getTurfById(id);
        if (turf.isPresent() && turf.get().getIsActive()) {
            return ResponseEntity.ok(turf.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Get turfs by type
     */
    @GetMapping("/type/{turfType}")
    public ResponseEntity<List<Turf>> getTurfsByType(@PathVariable Turf.TurfType turfType) {
        List<Turf> turfs = turfService.getTurfsByType(turfType);
        return ResponseEntity.ok(turfs);
    }
    
    /**
     * Search turfs by location
     */
    @GetMapping("/search/location")
    public ResponseEntity<List<Turf>> searchTurfsByLocation(@RequestParam String location) {
        List<Turf> turfs = turfService.searchTurfsByLocation(location);
        return ResponseEntity.ok(turfs);
    }
    
    /**
     * Get turfs within price range
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<Turf>> getTurfsByPriceRange(
            @RequestParam BigDecimal minPrice, 
            @RequestParam BigDecimal maxPrice) {
        List<Turf> turfs = turfService.getTurfsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(turfs);
    }
    
    /**
     * Get top rated turfs
     */
    @GetMapping("/top-rated")
    public ResponseEntity<List<Turf>> getTopRatedTurfs(@RequestParam(defaultValue = "10") int limit) {
        List<Turf> turfs = turfService.getTopRatedTurfs(limit);
        return ResponseEntity.ok(turfs);
    }
    
    /**
     * Get featured turfs for homepage
     */
    @GetMapping("/featured")
    public ResponseEntity<List<Turf>> getFeaturedTurfs() {
        List<Turf> turfs = turfService.getFeaturedTurfs();
        return ResponseEntity.ok(turfs);
    }
    
    /**
     * Advanced search with multiple filters
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Turf>> searchTurfs(
            @RequestParam(required = false) Turf.TurfType turfType,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        Page<Turf> turfs = turfService.searchTurfsWithFilters(
            turfType, city, minPrice, maxPrice, minRating, 
            page, size, sortBy, sortDirection);
        
        return ResponseEntity.ok(turfs);
    }
    
    /**
     * Find nearby turfs using coordinates
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<Turf>> findNearbyTurfs(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "10.0") double radiusKm) {
        List<Turf> turfs = turfService.findNearbyTurfs(latitude, longitude, radiusKm);
        return ResponseEntity.ok(turfs);
    }
    
    /**
     * Get turf statistics by city
     */
    @GetMapping("/stats/by-city")
    public ResponseEntity<List<Object[]>> getTurfStatsByCity() {
        List<Object[]> stats = turfService.getTurfStatsByCity();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Check if turf is available
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<Map<String, Boolean>> checkTurfAvailability(@PathVariable Long id) {
        boolean available = turfService.isTurfAvailable(id);
        return ResponseEntity.ok(Map.of("available", available));
    }
    
    // Admin endpoints
    
    /**
     * Create new turf (Admin only)
     */
    @PostMapping
    public ResponseEntity<Turf> createTurf(@RequestBody Turf turf) {
        try {
            Turf savedTurf = turfService.createTurf(turf);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTurf);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }
    
    /**
     * Update turf (Admin only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Turf> updateTurf(@PathVariable Long id, @RequestBody Turf turfDetails) {
        try {
            Turf updatedTurf = turfService.updateTurf(id, turfDetails);
            return ResponseEntity.ok(updatedTurf);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Delete turf (Admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTurf(@PathVariable Long id) {
        try {
            turfService.deleteTurf(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}