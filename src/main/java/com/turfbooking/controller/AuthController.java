package com.turfbooking.controller;

import com.turfbooking.model.User;
import com.turfbooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

	@Autowired
	private UserService userService;

	@Value("${app.allow-self-promotion:false}")
	private boolean allowSelfPromotion;

	@GetMapping("/me")
	public ResponseEntity<Map<String, Object>> me(Authentication authentication) {
		if (authentication == null) {
			return ResponseEntity.ok(Map.of("authenticated", false));
		}
		String email = authentication.getName();
		Optional<User> user = userService.findByEmail(email);
		Map<String, Object> userInfo = user.map(u -> {
			return Map.<String, Object>of(
				"authenticated", true,
				"id", u.getId(),
				"name", u.getName(),
				"email", u.getEmail(),
				"role", u.getRole().name(),
				"picture", u.getProfilePicture()
			);
		}).orElse(Map.of("authenticated", false));
		return ResponseEntity.ok(userInfo);
	}

	@PostMapping("/promote")
	public ResponseEntity<Map<String, Object>> promote(@RequestParam Long userId) {
		userService.promoteToAdmin(userId);
		return ResponseEntity.ok(Map.of("ok", true));
	}

	@PostMapping("/promote-self")
	public ResponseEntity<Map<String, Object>> promoteSelf(Authentication authentication) {
		if (!allowSelfPromotion || authentication == null) {
			return ResponseEntity.status(403).build();
		}
		String email = authentication.getName();
		Optional<User> user = userService.findByEmail(email);
		if (user.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}
		userService.promoteToAdmin(user.get().getId());
		return ResponseEntity.ok(Map.of("ok", true));
	}
}
