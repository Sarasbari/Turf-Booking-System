package com.turfbooking.controller;

import com.turfbooking.model.Transaction;
import com.turfbooking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	@GetMapping("/{id}")
	public ResponseEntity<Transaction> getById(@PathVariable Long id) {
		Optional<Transaction> tx = transactionService.getTransactionById(id);
		return tx.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/booking/{bookingId}")
	public ResponseEntity<List<Transaction>> getByBooking(@PathVariable Long bookingId) {
		return ResponseEntity.ok(transactionService.getTransactionsByBooking(bookingId));
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Transaction>> getByUser(@PathVariable Long userId) {
		return ResponseEntity.ok(transactionService.getUserTransactions(userId));
	}

	@PostMapping("/pay")
	public ResponseEntity<Transaction> pay(@RequestBody Map<String, Object> payload) {
		try {
			Long bookingId = Long.valueOf(payload.get("bookingId").toString());
			Transaction.PaymentMethod method = Transaction.PaymentMethod.valueOf(payload.get("method").toString());
			String details = payload.get("details") == null ? "{}" : payload.get("details").toString();
			Transaction tx = transactionService.processPayment(bookingId, method, details);
			return ResponseEntity.status(HttpStatus.CREATED).body(tx);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PostMapping("/refund/{bookingId}")
	public ResponseEntity<Void> refund(@PathVariable Long bookingId) {
		try {
			transactionService.processRefund(bookingId);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@GetMapping("/range")
	public ResponseEntity<List<Transaction>> byRange(@RequestParam String start, @RequestParam String end) {
		try {
			LocalDateTime s = LocalDateTime.parse(start);
			LocalDateTime e = LocalDateTime.parse(end);
			return ResponseEntity.ok(transactionService.getTransactionsByDateRange(s, e));
		} catch (Exception ex) {
			return ResponseEntity.badRequest().build();
		}
	}
}


