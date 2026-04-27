package com.tss.aml.controller;

import com.tss.aml.service.DataIngestionService;
import com.tss.aml.service.RuleEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final RuleEngineService ruleEngineService;

    @PostMapping("/customers")
    @PreAuthorize("hasRole('BANK_ADMIN')")
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

    @PostMapping("/transactions")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<String> uploadTransactions(@RequestParam("file") MultipartFile file) {

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

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();

            Files.createDirectories(uploadPath);

            Path targetLocation = uploadPath.resolve(fileName);

            System.out.println("Saving to: " + targetLocation);

            file.transferTo(targetLocation);

            dataIngestionService.ingestTransactionsFromFile(file);

            ruleEngineService.applyRules();

            return ResponseEntity.ok("Uploaded: " + fileName);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}