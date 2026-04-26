package com.tss.aml.controller;

import com.tss.aml.context.TenantContext;
import com.tss.aml.dto.request.SelectedRuleRegisterDto;
import com.tss.aml.dto.result.AlertResponseDto;
import com.tss.aml.dto.result.SelectedRuleResponseDto;
import com.tss.aml.service.RuleService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<SelectedRuleResponseDto>> getRules(@PathVariable String tenant){
        try {
            TenantContext.setTenant("tenant_" + tenant);
            return ResponseEntity.ok(ruleService.getSelectedRules());
        } finally {
            TenantContext.clear();
        }
    }

    @GetMapping("/alerts")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<List<AlertResponseDto>> getAlerts(){
        return ResponseEntity.ok(ruleService.getAlerts());
    }
}
