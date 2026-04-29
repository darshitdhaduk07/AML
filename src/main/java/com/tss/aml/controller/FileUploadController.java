package com.tss.aml.controller;

import com.tss.aml.service.DataIngestionService;
import com.tss.aml.service.RuleEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.tss.aml.constant.GlobalConstants.UPLOAD_DIR;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {

    private final DataIngestionService dataIngestionService;
    private final RuleEngineService ruleEngineService;

    @PostMapping("/customers")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        log.info("Received request to upload customer file: {}", file.getOriginalFilename());
        if (file.isEmpty()) {
            log.warn("Upload failed: File is empty");
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

            log.debug("Saving file to: {}", targetLocation);
            file.transferTo(targetLocation);

            dataIngestionService.ingestCustomersFromFile(file);

            log.info("Successfully uploaded and processed customer file: {}", fileName);
            return ResponseEntity.ok("Uploaded: " + fileName);

        } catch (Exception e) {
            log.error("Error during customer file upload: {}", fileName, e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

    @PostMapping("/transactions")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    @Transactional
    public ResponseEntity<String> uploadTransactions(@RequestParam("file") MultipartFile file) {
        log.info("Received request to upload transaction file: {}", file.getOriginalFilename());
        if (file.isEmpty()) {
            log.warn("Upload failed: File is empty");
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

            log.debug("Saving file to: {}", targetLocation);
            file.transferTo(targetLocation);

            dataIngestionService.ingestTransactionsFromFile(file);

            ruleEngineService.applyRules();

            log.info("Successfully uploaded and processed transaction file: {}", fileName);
            return ResponseEntity.ok("Uploaded: " + fileName);

        } catch (Exception e) {
            log.error("Error during transaction file upload: {}", fileName, e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}