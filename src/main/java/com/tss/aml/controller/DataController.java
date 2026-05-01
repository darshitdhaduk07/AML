package com.tss.aml.controller;

import com.tss.aml.dto.result.ComplianceOfficerResponseDto;
import com.tss.aml.dto.result.PaginatedResponseDto;
import com.tss.aml.dto.result.RuleTemplateResponseDto;
import com.tss.aml.dto.result.TenantResponseDto;
import com.tss.aml.service.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
public class DataController {

    private final DataService dataService;

    @GetMapping("/tenants")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<PaginatedResponseDto<TenantResponseDto>> getTenants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(dataService.getTenants(pageable));
    }

    @GetMapping("/rule-templates")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<List<RuleTemplateResponseDto>> getRuleTemplates(){
        return ResponseEntity.ok(dataService.getRuleTemplates());
    }

    @GetMapping("/compliance-officers")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<PaginatedResponseDto<ComplianceOfficerResponseDto>> getComplianceOfficers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(dataService.getComplianceOfficers(pageable));
    }

}
