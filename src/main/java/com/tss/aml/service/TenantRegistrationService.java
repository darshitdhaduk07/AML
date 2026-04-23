package com.tss.aml.service;

import com.tss.aml.dto.request.RegisterRequestDto;
import com.tss.aml.master.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantRegistrationService {

    private final TenantRepository tenantRepository;
    private final MigrationService migrationService;
    private final BankAdminRegistrationService bankAdminRegistrationService;

    public void registerTenant(RegisterRequestDto request) {

        String schemaName = generateSchemaName(request.getTenantName());

        if (tenantRepository.findBySchemaName(schemaName).isPresent()) {
            throw new RuntimeException("Tenant already exists");
        }

        migrationService.runFlyWay(schemaName);
        migrationService.saveTenant(schemaName, request);
        bankAdminRegistrationService.registerAdmin(request);
    }

    private String generateSchemaName(String tenantName) {
        String clean = tenantName.toLowerCase()
                .replaceAll("[^a-z0-9]", "_");

        return "tenant_" + clean;
    }
}