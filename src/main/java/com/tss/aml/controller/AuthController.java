package com.tss.aml.controller;

import com.tss.aml.dto.request.ComplianceOfficerRegisterRequestDto;
import com.tss.aml.dto.request.TenantRegisterRequestDto;
import com.tss.aml.dto.request.VerifyRequest;
import com.tss.aml.service.ComplianceOfficerRegistrationService;
import com.tss.aml.service.JwtService;
import com.tss.aml.service.TenantRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final TenantRegistrationService tenantRegistrationService;
    private final ComplianceOfficerRegistrationService complianceOfficerRegistrationService;
    private final JwtService jwtService;

    @PostMapping("/register/tenant")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<String> registerTenant(@RequestBody TenantRegisterRequestDto request) {
        log.info("Request received to register tenant: {}", request.getTenantName());
        tenantRegistrationService.registerTenant(request);
        log.info("Tenant {} registered successfully", request.getTenantName());
        return ResponseEntity.ok("Tenant Registered Successfully");
    }

    @PostMapping("/register/co")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<String> registerComplianceOfficer(@RequestBody ComplianceOfficerRegisterRequestDto request){
        log.info("Request received to register compliance officer: {}", request.getEmail());
        complianceOfficerRegistrationService.registerCO(request);
        log.info("Compliance officer {} registered successfully", request.getEmail());
        return ResponseEntity.ok("Compliance Officer Registered Successfully");
    }

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verifyJwt(@RequestBody VerifyRequest request){
        log.info("Request received to verify JWT token");
        return ResponseEntity.ok(jwtService.verifyToken(request.getAuth_token()));
    }
}
