package com.turfbooking.controller;

import com.turfbooking.model.Tournament;
import com.turfbooking.model.TournamentRegistration;
import com.turfbooking.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tournaments")
@CrossOrigin(origins = "*")
public class TournamentController {

	@Autowired
	private TournamentService tournamentService;

	@GetMapping
	public ResponseEntity<List<Tournament>> getAll() {
		return ResponseEntity.ok(tournamentService.getAllTournaments());
	}

	@GetMapping("/upcoming")
	public ResponseEntity<List<Tournament>> upcoming() {
		return ResponseEntity.ok(tournamentService.getUpcomingTournaments());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Tournament> getById(@PathVariable Long id) {
		Optional<Tournament> t = tournamentService.getTournamentById(id);
		return t.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Tournament> create(@RequestBody Tournament tournament) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(tournamentService.createTournament(tournament));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<Tournament> update(@PathVariable Long id, @RequestBody Tournament tournament) {
		try {
			return ResponseEntity.ok(tournamentService.updateTournament(id, tournament));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PostMapping("/{id}/register")
	public ResponseEntity<TournamentRegistration> register(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
		try {
			Long userId = Long.valueOf(payload.get("userId").toString());
			String teamName = payload.get("teamName").toString();
			String teamMembers = payload.get("teamMembers").toString();
			String contactPhone = payload.get("contactPhone").toString();
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(tournamentService.registerTeam(id, userId, teamName, teamMembers, contactPhone));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@DeleteMapping("/registrations/{registrationId}")
	public ResponseEntity<Void> cancelRegistration(@PathVariable Long registrationId, @RequestParam Long userId) {
		try {
			tournamentService.cancelRegistration(registrationId, userId);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
}


