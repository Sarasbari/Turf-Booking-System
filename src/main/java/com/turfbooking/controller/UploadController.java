package com.turfbooking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/uploads")
@CrossOrigin(origins = "*")
public class UploadController {

	@PostMapping(consumes = { "multipart/form-data" })
	public ResponseEntity<Map<String, String>> upload(@RequestPart("file") MultipartFile file) throws IOException {
		if (file.isEmpty()) return ResponseEntity.badRequest().build();
		String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
		String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
		String name = UUID.randomUUID().toString().replaceAll("-", "") + ext;
		Path dir = Paths.get("uploads");
		Files.createDirectories(dir);
		Path dest = dir.resolve(name);
		file.transferTo(dest.toFile());
		return ResponseEntity.ok(Map.of("url", "/uploads/" + name));
	}
}


