package com.tss.aml.controller;

import com.tss.aml.service.DataIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.tss.aml.constant.GlobalConstants.UPLOAD_DIR;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final DataIngestionService dataIngestionService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            return ResponseEntity.badRequest().body("Only CSV files are allowed");
        }

        String contentType = file.getContentType();

        if (contentType != null &&
                !contentType.equals("text/csv") &&
                !contentType.equals("application/vnd.ms-excel")) {

            return ResponseEntity.badRequest().body("Invalid file type");
        }

        // save file here
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();

            Files.createDirectories(uploadPath);

            Path targetLocation = uploadPath.resolve(fileName);

            System.out.println("Saving to: " + targetLocation);

            file.transferTo(targetLocation);

            dataIngestionService.ingestCustomersFromFile(file);

            return ResponseEntity.ok("Uploaded: " + fileName);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}