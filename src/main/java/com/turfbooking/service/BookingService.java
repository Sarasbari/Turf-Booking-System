package com.turfbooking.service;

import com.turfbooking.model.Booking;
import com.turfbooking.model.Turf;
import com.turfbooking.model.User;
import com.turfbooking.model.Transaction;
import com.turfbooking.repository.BookingRepository;
import com.turfbooking.repository.TurfRepository;
import com.turfbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Booking entity operations
 * Handles booking creation, validation, conflict detection, and management
 */
@Service
@Transactional
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private TurfRepository turfRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionService transactionService;
    
    /**
     * Get all bookings
     */
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    /**
     * Get booking by ID
     */
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }
    
    /**
     * Get user's bookings
     */
    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Get user's bookings with pagination
     */
    public Page<Booking> getUserBookingsWithPagination(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    /**
     * Get upcoming bookings for a user
     */
    public List<Booking> getUserUpcomingBookings(Long userId) {
        return bookingRepository.findUpcomingBookingsByUserId(userId, LocalDate.now());
    }
    
    /**
     * Get bookings by turf
     */
    public List<Booking> getBookingsByTurf(Long turfId) {
        return bookingRepository.findByTurfId(turfId);
    }
    
    /**
     * Get bookings by status
     */
    public List<Booking> getBookingsByStatus(Booking.BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }
    
    /**
     * Get turf bookings for a specific date
     */
    public List<Booking> getTurfBookingsForDate(Long turfId, LocalDate date) {
        return bookingRepository.findTurfBookingsForDate(turfId, date);
    }
    
    /**
     * Create new booking with validation
     */
    @Transactional
    public Booking createBooking(Long userId, Long turfId, LocalDate bookingDate, 
                               LocalTime startTime, LocalTime endTime, 
                               Integer numberOfPlayers, String specialRequests) {
        
        // Validate user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Validate turf
        Turf turf = turfRepository.findById(turfId)
            .orElseThrow(() -> new RuntimeException("Turf not found with id: " + turfId));
        
        if (!turf.getIsActive()) {
            throw new RuntimeException("Turf is not active");
        }
        
        // Validate booking date (must be today or future)
        if (bookingDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot book for past dates");
        }
        
        // Validate time slot
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new RuntimeException("Invalid time slot");
        }
        
        // Check if time slot is within turf operating hours
        if (startTime.isBefore(turf.getOpeningTime()) || endTime.isAfter(turf.getClosingTime())) {
            throw new RuntimeException("Booking time is outside turf operating hours");
        }
        
        // Check for conflicting bookings
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
            turfId, bookingDate, startTime, endTime);
        
        if (!conflictingBookings.isEmpty()) {
            throw new RuntimeException("Time slot is already booked");
        }
        
        // Calculate total amount
        BigDecimal totalAmount = calculateBookingAmount(turf.getPricePerHour(), startTime, endTime);
        
        // Create booking
        Booking booking = new Booking(user, turf, bookingDate, startTime, endTime, 
                                    numberOfPlayers, totalAmount);
        booking.setSpecialRequests(specialRequests);
        booking.setStatus(Booking.BookingStatus.PENDING);
        
        return bookingRepository.save(booking);
    }
    
    /**
     * Calculate booking amount based on duration
     */
    private BigDecimal calculateBookingAmount(BigDecimal pricePerHour, LocalTime startTime, LocalTime endTime) {
        long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
        double hours = minutes / 60.0;
        return pricePerHour.multiply(BigDecimal.valueOf(hours));
    }
    
    /**
     * Update booking status
     */
    public Booking updateBookingStatus(Long bookingId, Booking.BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
        
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }
    
    /**
     * Cancel booking
     */
    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
        
        // Check if user owns this booking
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only cancel your own bookings");
        }
        
        // Check if booking can be cancelled
        if (booking.getStatus() == Booking.BookingStatus.COMPLETED ||
            booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel this booking");
        }
        
        // Check cancellation policy (e.g., can't cancel within 2 hours of start time)
        LocalDate today = LocalDate.now();
        if (booking.getBookingDate().equals(today)) {
            LocalTime currentTime = LocalTime.now();
            if (ChronoUnit.HOURS.between(currentTime, booking.getStartTime()) < 2) {
                throw new RuntimeException("Cannot cancel booking less than 2 hours before start time");
            }
        }
        
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        
        // Process refund if payment was made
        transactionService.processRefund(bookingId);
    }
    
    /**
     * Complete booking
     */
    public Booking completeBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
        
        booking.setStatus(Booking.BookingStatus.COMPLETED);
        return bookingRepository.save(booking);
    }
    
    /**
     * Check if time slot is available
     */
    public boolean isTimeSlotAvailable(Long turfId, LocalDate bookingDate, 
                                     LocalTime startTime, LocalTime endTime) {
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
            turfId, bookingDate, startTime, endTime);
        return conflictingBookings.isEmpty();
    }
    
    /**
     * Get available time slots for a turf on a specific date
     */
    public List<String> getAvailableTimeSlots(Long turfId, LocalDate date) {
        Turf turf = turfRepository.findById(turfId)
            .orElseThrow(() -> new RuntimeException("Turf not found"));
        
        List<Booking> existingBookings = bookingRepository.findTurfBookingsForDate(turfId, date);
        
        // Generate available slots (implementation would create time slots based on 
        // turf operating hours and existing bookings)
        // This is a simplified version - you'd implement proper slot generation logic
        return List.of("09:00-10:00", "10:00-11:00", "11:00-12:00"); // Example
    }
    
    /**
     * Get booking statistics
     */
    public List<Object[]> getBookingStatsByStatus() {
        return bookingRepository.countBookingsByStatus();
    }
    
    /**
     * Get popular turfs by booking count
     */
    public List<Object[]> getPopularTurfs(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return bookingRepository.findPopularTurfs(pageable);
    }
    
    /**
     * Calculate total revenue
     */
    public Double getTotalRevenue() {
        return bookingRepository.calculateTotalRevenue();
    }
    
    /**
     * Calculate revenue for date range
     */
    public Double getRevenueForPeriod(LocalDate startDate, LocalDate endDate) {
        return bookingRepository.calculateRevenueForPeriod(startDate, endDate);
    }
    
    /**
     * Get bookings by date range and status
     */
    public List<Booking> getBookingsByDateRangeAndStatus(LocalDate startDate, LocalDate endDate, 
                                                       Booking.BookingStatus status) {
        return bookingRepository.findBookingsByDateRangeAndStatus(startDate, endDate, status);
    }
}