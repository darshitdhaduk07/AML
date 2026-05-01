package com.tss.aml.controller;

import com.tss.aml.dto.request.CaseRequestDto;
import com.tss.aml.dto.request.ComplianceInvestigationAssignmentDto;
import com.tss.aml.dto.result.ComplianceInvestigationAssignmentResponseDto;
import com.tss.aml.service.InvestigationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PutMapping("/mark-false-positive/{brokenRuleId}")
    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    public ResponseEntity<String> markFalsePositive(@PathVariable UUID brokenRuleId){
        investigationService.markFalsePositive(brokenRuleId);
        return ResponseEntity.ok("Marked False Positive");
    }

    @GetMapping("/assignments")
    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    public ResponseEntity<List<ComplianceInvestigationAssignmentResponseDto>> getAssignments(){
        return ResponseEntity.ok(investigationService.getAssignments());
    }

    @PutMapping("/cases/{caseId}/escalate")
    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    public ResponseEntity<String> escalateCase(@PathVariable UUID caseId){
        investigationService.escalateCase(caseId);
        return ResponseEntity.ok("Case Escalated.");
    }

    @PutMapping("/cases/{caseId}/file-sar")
    @PreAuthorize("hasRole('COMPLIANCE_OFFICER')")
    public ResponseEntity<String> fileSar(@PathVariable UUID caseId){
        investigationService.fileSAR(caseId);
        return ResponseEntity.ok("SAR/STR Filed.");
    }
}
