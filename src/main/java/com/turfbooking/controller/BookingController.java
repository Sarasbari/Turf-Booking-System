package com.turfbooking.controller;

import com.turfbooking.model.Booking;
import com.turfbooking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for Booking operations
 */
@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

	@Autowired
	private BookingService bookingService;

	@GetMapping
	public ResponseEntity<List<Booking>> getAllBookings() {
		return ResponseEntity.ok(bookingService.getAllBookings());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
		Optional<Booking> booking = bookingService.getBookingById(id);
		return booking.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Booking>> getUserBookings(@PathVariable Long userId) {
		return ResponseEntity.ok(bookingService.getUserBookings(userId));
	}

	@GetMapping("/turf/{turfId}")
	public ResponseEntity<List<Booking>> getTurfBookingsForDate(@PathVariable Long turfId,
			@RequestParam(required = false) String date) {
		if (date != null) {
			return ResponseEntity.ok(bookingService.getTurfBookingsForDate(turfId, LocalDate.parse(date)));
		}
		return ResponseEntity.ok(bookingService.getBookingsByTurf(turfId));
	}

	@GetMapping("/status/{status}")
	public ResponseEntity<List<Booking>> getBookingsByStatus(@PathVariable String status) {
		try {
			Booking.BookingStatus bookingStatus = Booking.BookingStatus.valueOf(status.toUpperCase());
			return ResponseEntity.ok(bookingService.getBookingsByStatus(bookingStatus));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@PostMapping
	public ResponseEntity<Booking> createBooking(@RequestBody Map<String, Object> payload) {
		try {
			Long userId = Long.valueOf(payload.get("userId").toString());
			Long turfId = Long.valueOf(payload.get("turfId").toString());
			LocalDate bookingDate = LocalDate.parse(payload.get("bookingDate").toString());
			LocalTime startTime = LocalTime.parse(payload.get("startTime").toString());
			LocalTime endTime = LocalTime.parse(payload.get("endTime").toString());
			Integer numberOfPlayers = payload.get("numberOfPlayers") == null ? 1
					: Integer.valueOf(payload.get("numberOfPlayers").toString());
			String specialRequests = payload.get("specialRequests") == null ? null
					: payload.get("specialRequests").toString();

			Booking saved = bookingService.createBooking(userId, turfId, bookingDate, startTime, endTime,
				numberOfPlayers, specialRequests);
			return ResponseEntity.status(HttpStatus.CREATED).body(saved);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<Booking> updateBookingStatus(@PathVariable Long id, @RequestParam String status) {
		try {
			Booking.BookingStatus bookingStatus = Booking.BookingStatus.valueOf(status.toUpperCase());
			return ResponseEntity.ok(bookingService.updateBookingStatus(id, bookingStatus));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> cancelBooking(@PathVariable Long id, @RequestParam Long userId) {
		try {
			bookingService.cancelBooking(id, userId);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@GetMapping("/availability")
	public ResponseEntity<Map<String, Boolean>> checkAvailability(@RequestParam Long turfId,
			@RequestParam String date, @RequestParam String startTime, @RequestParam String endTime) {
		try {
			boolean available = bookingService.isTimeSlotAvailable(turfId, LocalDate.parse(date),
					LocalTime.parse(startTime), LocalTime.parse(endTime));
			return ResponseEntity.ok(Map.of("available", available));
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}
}