package com.tss.aml.controller;

import com.tss.aml.dto.request.CaseRequestDto;
import com.tss.aml.dto.request.ComplianceInvestigationAssignmentDto;
import com.tss.aml.dto.result.CaseResponseDto;
import com.tss.aml.dto.result.ComplianceInvestigationAssignmentResponseDto;
import com.tss.aml.dto.result.PaginatedResponseDto;
import com.tss.aml.service.InvestigationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/investigation")
@RequiredArgsConstructor
public class InvestigationController {

    private final InvestigationService investigationService;

    @PostMapping("/assignments")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<String> assignComplianceOfficer(@RequestBody ComplianceInvestigationAssignmentDto request){
        investigationService.assignComplianceOfficer(request);
        return ResponseEntity.ok("Customer assigned to Compliance officer for Investigation.");
    }

    @PostMapping("/cases")
    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    public ResponseEntity<String> createCase(@RequestBody CaseRequestDto request){
        investigationService.createCase(request);
        return ResponseEntity.ok("Case Created.");
    }

    @GetMapping("/cases")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'BANK_ADMIN')")
    public ResponseEntity<PaginatedResponseDto<CaseResponseDto>> getCases(
            @RequestParam(required = false) com.tss.aml.enums.CaseStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(status != null ? 
                investigationService.getCasesByStatus(status, pageable) : 
                investigationService.getCases(pageable));
    }

    @GetMapping("/cases/open")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'BANK_ADMIN')")
    public ResponseEntity<Boolean> hasOpenCase(@RequestParam String customerNumber) {
        return ResponseEntity.ok(investigationService.hasOpenCase(customerNumber));
    }

    @GetMapping("/escalated-cases")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<PaginatedResponseDto<CaseResponseDto>> getEscalatedCases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(investigationService.getEscalatedCases(pageable));
    }

    @PutMapping("/mark-false-positive/{brokenRuleId}")
    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    public ResponseEntity<String> markFalsePositive(@PathVariable UUID brokenRuleId){
        investigationService.markFalsePositive(brokenRuleId);
        return ResponseEntity.ok("Marked False Positive");
    }

    @GetMapping("/assignments")
    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    public ResponseEntity<PaginatedResponseDto<ComplianceInvestigationAssignmentResponseDto>> getAssignments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(investigationService.getAssignments(pageable));
    }

    @PutMapping("/cases/{caseId}/escalate")
    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    public ResponseEntity<String> escalateCase(@PathVariable UUID caseId){
        investigationService.escalateCase(caseId);
        return ResponseEntity.ok("Case Escalated.");
    }

    @PutMapping("/cases/{caseId}/close")
    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    public ResponseEntity<String> closeCase(@PathVariable UUID caseId){
        investigationService.closeCase(caseId);
        return ResponseEntity.ok("Case Closed.");
    }

    @PutMapping("/cases/{caseId}/file-sar")
    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    public ResponseEntity<String> fileSar(@PathVariable UUID caseId){
        investigationService.fileSAR(caseId);
        return ResponseEntity.ok("SAR/STR Filed.");
    }
}
