package com.turfbooking.controller;

import com.turfbooking.model.Offer;
import com.turfbooking.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/offers")
@CrossOrigin(origins = "*")
public class OfferController {

	@Autowired
	private OfferService offerService;

	@GetMapping
	public ResponseEntity<List<Offer>> getAll() {
		return ResponseEntity.ok(offerService.getAllOffers());
	}

	@GetMapping("/active")
	public ResponseEntity<List<Offer>> active() {
		return ResponseEntity.ok(offerService.getActiveOffers());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Offer> getById(@PathVariable Long id) {
		Optional<Offer> offer = offerService.getOfferById(id);
		return offer.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Offer> create(@RequestBody Offer offer) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(offerService.createOffer(offer));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PostMapping("/apply")
	public ResponseEntity<Map<String, Object>> apply(@RequestBody Map<String, Object> payload) {
		try {
			String code = payload.get("code").toString();
			BigDecimal amount = new BigDecimal(payload.get("amount").toString());
			String turfType = payload.get("turfType").toString();
			BigDecimal finalAmount = offerService.applyOffer(code, amount, turfType);
			return ResponseEntity.ok(Map.of("finalAmount", finalAmount));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
}


