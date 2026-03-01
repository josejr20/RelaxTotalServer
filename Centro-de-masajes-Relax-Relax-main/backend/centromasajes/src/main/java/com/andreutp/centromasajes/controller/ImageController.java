package com.andreutp.centromasajes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/upload")
public class ImageController {
    private static final String UPLOAD_DIR = "uploads/promotions/";

    @PostMapping("/promotion-image")
    public ResponseEntity<String> uploadPromotionImage(@RequestParam("image") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("File must not be empty");
        }

        try {
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) directory.mkdirs();

            String original = file.getOriginalFilename();
            if (original == null || original.isBlank()) {
                throw new IllegalArgumentException("Invalid file name");
            }

            String fileName = System.currentTimeMillis() + "_" + Paths.get(original).getFileName().toString();
            Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
            Path targetPath = uploadPath.resolve(fileName).normalize();

            if (!targetPath.startsWith(uploadPath)) {
                throw new IllegalArgumentException("Invalid file path");
            }

            Files.write(targetPath, file.getBytes());

            return ResponseEntity.ok("/uploads/promotions/" + fileName);

        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().body(iae.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("ERROR SUBIENDO IMAGEN");
        }
    }
}
