package com.turfbooking.repository;

import com.turfbooking.model.Booking;
import com.turfbooking.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Booking entity operations
 * Handles booking queries, conflict detection, and user booking history
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    // Find bookings by user
    List<Booking> findByUserOrderByCreatedAtDesc(User user);
    
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Find bookings by turf
    List<Booking> findByTurfId(Long turfId);
    
    // Find bookings by status
    List<Booking> findByStatus(Booking.BookingStatus status);
    
    // Find bookings by date range
    List<Booking> findByBookingDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Check for conflicting bookings (same turf, overlapping time slots)
    @Query("SELECT b FROM Booking b WHERE b.turf.id = :turfId AND " +
           "b.bookingDate = :bookingDate AND " +
           "b.status IN ('PENDING', 'CONFIRMED') AND " +
           "((b.startTime < :endTime AND b.endTime > :startTime))")
    List<Booking> findConflictingBookings(@Param("turfId") Long turfId,
                                        @Param("bookingDate") LocalDate bookingDate,
                                        @Param("startTime") LocalTime startTime,
                                        @Param("endTime") LocalTime endTime);
    
    // Find user's bookings with pagination
    Page<Booking> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // Find upcoming bookings for a user
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND " +
           "b.bookingDate >= :currentDate AND " +
           "b.status IN ('PENDING', 'CONFIRMED') " +
           "ORDER BY b.bookingDate ASC, b.startTime ASC")
    List<Booking> findUpcomingBookingsByUserId(@Param("userId") Long userId, 
                                              @Param("currentDate") LocalDate currentDate);
    
    // Find bookings for a specific turf on a specific date
    @Query("SELECT b FROM Booking b WHERE b.turf.id = :turfId AND " +
           "b.bookingDate = :bookingDate AND " +
           "b.status IN ('PENDING', 'CONFIRMED') " +
           "ORDER BY b.startTime ASC")
    List<Booking> findTurfBookingsForDate(@Param("turfId") Long turfId, 
                                         @Param("bookingDate") LocalDate bookingDate);
    
    // Find bookings by date range and status
    @Query("SELECT b FROM Booking b WHERE b.bookingDate BETWEEN :startDate AND :endDate " +
           "AND (:status IS NULL OR b.status = :status) " +
           "ORDER BY b.bookingDate DESC, b.startTime DESC")
    List<Booking> findBookingsByDateRangeAndStatus(@Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate,
                                                  @Param("status") Booking.BookingStatus status);
    
    // Count bookings by status
    @Query("SELECT b.status, COUNT(b) FROM Booking b GROUP BY b.status")
    List<Object[]> countBookingsByStatus();
    
    // Find popular turfs by booking count
    @Query("SELECT b.turf, COUNT(b) as bookingCount FROM Booking b " +
           "WHERE b.status = 'COMPLETED' " +
           "GROUP BY b.turf ORDER BY bookingCount DESC")
    List<Object[]> findPopularTurfs(Pageable pageable);
    
    // Calculate total revenue
    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.status = 'COMPLETED'")
    Double calculateTotalRevenue();
    
    // Calculate revenue for date range
    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.status = 'COMPLETED' " +
           "AND b.bookingDate BETWEEN :startDate AND :endDate")
    Double calculateRevenueForPeriod(@Param("startDate") LocalDate startDate, 
                                    @Param("endDate") LocalDate endDate);
}