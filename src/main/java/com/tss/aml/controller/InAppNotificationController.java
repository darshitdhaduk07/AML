package com.tss.aml.controller;

import com.tss.aml.dto.result.InAppNotificationResponseDto;
import com.tss.aml.enums.Role;
import com.tss.aml.service.InAppNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class InAppNotificationController {

    private final InAppNotificationService inAppNotificationService;

    @GetMapping("/bank-admin")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<List<InAppNotificationResponseDto>> getBankAdminNotifications() {
        return ResponseEntity.ok(inAppNotificationService.getNotificationsForRole(Role.BANK_ADMIN));
    }

    @GetMapping("/compliance-officer")
    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    public ResponseEntity<List<InAppNotificationResponseDto>> getComplianceOfficerNotifications() {
        return ResponseEntity.ok(inAppNotificationService.getNotificationsForRole(Role.COMPLIANCE_OFFICER));
    }


    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markComplianceOfficerAsRead(@PathVariable UUID id) {
        inAppNotificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
}
