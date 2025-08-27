package com.turfbooking.controller;

import com.turfbooking.model.Review;
import com.turfbooking.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

	@Autowired
	private ReviewService reviewService;

	@GetMapping("/{id}")
	public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
		Optional<Review> review = reviewService.getReviewById(id);
		return review.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/turf/{turfId}")
	public ResponseEntity<List<Review>> getTurfReviews(@PathVariable Long turfId) {
		return ResponseEntity.ok(reviewService.getTurfReviews(turfId));
	}

	@GetMapping("/turf/{turfId}/page")
	public ResponseEntity<Page<Review>> getTurfReviewsPaged(@PathVariable Long turfId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return ResponseEntity.ok(reviewService.getTurfReviewsWithPagination(turfId, page, size));
	}

	@GetMapping("/recent")
	public ResponseEntity<List<Review>> getRecent(@RequestParam(defaultValue = "5") int limit) {
		return ResponseEntity.ok(reviewService.getRecentReviews(limit));
	}

	@PostMapping
	public ResponseEntity<Review> createReview(@RequestBody Map<String, Object> payload) {
		try {
			Long userId = Long.valueOf(payload.get("userId").toString());
			Long turfId = Long.valueOf(payload.get("turfId").toString());
			Long bookingId = payload.get("bookingId") == null ? null : Long.valueOf(payload.get("bookingId").toString());
			Integer rating = Integer.valueOf(payload.get("rating").toString());
			String reviewText = payload.get("reviewText") == null ? null : payload.get("reviewText").toString();
			Review review = reviewService.createReview(userId, turfId, bookingId, rating, reviewText);
			return ResponseEntity.status(HttpStatus.CREATED).body(review);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
		try {
			Long userId = Long.valueOf(payload.get("userId").toString());
			Integer rating = Integer.valueOf(payload.get("rating").toString());
			String reviewText = payload.get("reviewText") == null ? null : payload.get("reviewText").toString();
			return ResponseEntity.ok(reviewService.updateReview(id, userId, rating, reviewText));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteReview(@PathVariable Long id, @RequestParam Long userId) {
		try {
			reviewService.deleteReview(id, userId);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
}


