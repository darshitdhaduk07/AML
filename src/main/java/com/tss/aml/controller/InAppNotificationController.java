package com.tss.aml.controller;

import com.tss.aml.dto.result.InAppNotificationResponseDto;
import com.tss.aml.dto.result.PaginatedResponseDto;
import com.tss.aml.enums.Role;
import com.tss.aml.service.InAppNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class InAppNotificationController {

    private final InAppNotificationService inAppNotificationService;

    @GetMapping("/bank-admin")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<PaginatedResponseDto<InAppNotificationResponseDto>> getBankAdminNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(inAppNotificationService.getNotificationsForRole(Role.BANK_ADMIN, pageable));
    }

    @GetMapping("/compliance-officer")
    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    public ResponseEntity<PaginatedResponseDto<InAppNotificationResponseDto>> getComplianceOfficerNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(inAppNotificationService.getNotificationsForRole(Role.COMPLIANCE_OFFICER, pageable));
    }


    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markComplianceOfficerAsRead(@PathVariable UUID id) {
        inAppNotificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
}
