package com.tss.aml.controller;

import com.tss.aml.context.TenantContext;
import com.tss.aml.dto.request.SelectedRuleRegisterDto;
import com.tss.aml.dto.result.AlertResponseDto;
import com.tss.aml.dto.result.PaginatedResponseDto;
import com.tss.aml.dto.result.SelectedRuleResponseDto;
import com.tss.aml.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rules")
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    @PostMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @Transactional
    public ResponseEntity<String> createRule(@RequestBody SelectedRuleRegisterDto rule){
        try {
            TenantContext.setTenant("tenant_" + rule.getTenant());
            ruleService.registerRule(rule);
            return ResponseEntity.ok("Rule Created And Assigned Successfully.");
        } finally {
            TenantContext.clear();
        }
    }

    @GetMapping("/{tenant}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @Transactional
    public ResponseEntity<PaginatedResponseDto<SelectedRuleResponseDto>> getRules(
            @PathVariable String tenant,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        try {
            TenantContext.setTenant("tenant_" + tenant);
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(ruleService.getSelectedRules(pageable));
        } finally {
            TenantContext.clear();
        }
    }

    @GetMapping("/alerts")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<PaginatedResponseDto<AlertResponseDto>> getAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ruleService.getAlerts(pageable));
    }

    @GetMapping("/alerts/all")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<List<AlertResponseDto>> getAllAlertsSorted(){
        return ResponseEntity.ok(ruleService.getAllAlertsSorted());
    }

    @GetMapping("/alerts/customer/{customerNumber}")
    @PreAuthorize("hasAnyRole('BANK_ADMIN', 'COMPLIANCE_OFFICER')")
    public ResponseEntity<List<AlertResponseDto>> getActiveAlertsForCustomer(@PathVariable String customerNumber){
        return ResponseEntity.ok(ruleService.getActiveAlertsForCustomer(customerNumber));
    }
}
