package com.tss.aml.controller;

import com.tss.aml.dto.request.RegisterRequestDto;
import com.tss.aml.service.TenantRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TenantRegistrationService service;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDto request) {
        service.registerTenant(request);
        return ResponseEntity.ok("Tenant Registered Successfully");
    }

    @PostMapping("/hi")
    public ResponseEntity<String> hi(){
        return ResponseEntity.ok("Hi");
    }
}
