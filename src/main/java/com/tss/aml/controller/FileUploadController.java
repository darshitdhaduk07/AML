package com.tss.aml.controller;

import com.tss.aml.enums.FileType;
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
    public ResponseEntity<String> uploadCustomers(@RequestParam("file") MultipartFile file) {
        return upload(FileType.CUSTOMER, file);
    }

    @PostMapping("/transactions")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    @Transactional
    public ResponseEntity<String> uploadTransactions(@RequestParam("file") MultipartFile file) {
        return upload(FileType.TRANSACTION, file);
    }

    public ResponseEntity<String> upload(FileType type, MultipartFile file){
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

            if(type.equals(FileType.CUSTOMER))
                dataIngestionService.ingestCustomersFromFile(file);
            else
                dataIngestionService.ingestTransactionsFromFile(file);

            ruleEngineService.applyRules();

            log.info("Successfully uploaded and processed {} file: {}", type, fileName);
            return ResponseEntity.ok("Uploaded: " + fileName);

        } catch (Exception e) {
            log.error("Error during {} file upload: {}", type, fileName, e);
            String message = (e instanceof com.tss.aml.exception.BulkValidationException) ? 
                "Validation failed. Please check the Batch Summary for details." : e.getMessage();
            return ResponseEntity.internalServerError().body(message);
        }
    }

}