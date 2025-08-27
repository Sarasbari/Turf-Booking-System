package com.turfbooking.controller;

import com.turfbooking.model.Turf;
import com.turfbooking.model.Tournament;
import com.turfbooking.model.Offer;
import com.turfbooking.service.TurfService;
import com.turfbooking.service.TournamentService;
import com.turfbooking.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

	@Autowired private TurfService turfService;
	@Autowired private TournamentService tournamentService;
	@Autowired private OfferService offerService;

	@GetMapping("/turfs")
	public ResponseEntity<List<Turf>> allTurfs() { return ResponseEntity.ok(turfService.getAllActiveTurfs()); }

	@PostMapping("/turfs")
	public ResponseEntity<Turf> createTurf(@RequestBody Turf turf) { return ResponseEntity.ok(turfService.createTurf(turf)); }

	@PutMapping("/turfs/{id}")
	public ResponseEntity<Turf> updateTurf(@PathVariable Long id, @RequestBody Turf turf) { return ResponseEntity.ok(turfService.updateTurf(id, turf)); }

	@GetMapping("/tournaments")
	public ResponseEntity<List<Tournament>> allTournaments() { return ResponseEntity.ok(tournamentService.getAllTournaments()); }

	@PostMapping("/tournaments")
	public ResponseEntity<Tournament> createTournament(@RequestBody Tournament tournament) {
		return ResponseEntity.ok(tournamentService.createTournament(tournament));
	}

	@PutMapping("/tournaments/{id}")
	public ResponseEntity<Tournament> updateTournament(@PathVariable Long id, @RequestBody Tournament tournament) {
		return ResponseEntity.ok(tournamentService.updateTournament(id, tournament));
	}

	@PostMapping("/offers")
	public ResponseEntity<Offer> createOffer(@RequestBody Offer offer) { return ResponseEntity.ok(offerService.createOffer(offer)); }
}


