package com.tss.aml.service;

import com.tss.aml.dto.request.TenantRegisterRequestDto;
import com.tss.aml.exception.BusinessException;
import com.tss.aml.context.TenantContext;
import com.tss.aml.dto.request.TenantRegisterRequestDto;
import com.tss.aml.master.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantRegistrationService {

    private final TenantRepository tenantRepository;
    private final MigrationService migrationService;
    private final BankAdminRegistrationService bankAdminRegistrationService;

    @Transactional
    public void registerTenant(TenantRegisterRequestDto request) {
        String schemaName = generateSchemaName(request.getTenantName());
        log.debug("Generated schema name: {}", schemaName);

        if (tenantRepository.findBySchemaName(schemaName).isPresent()) {
            log.error("Tenant registration failed: Tenant {} already exists", schemaName);
            throw new BusinessException("Tenant already exists");
        }

        log.info("Running FlyWay migrations for schema: {}", schemaName);
        migrationService.runFlyWay(schemaName);
        
        log.info("Saving tenant {} metadata", schemaName);
        migrationService.saveTenant(schemaName, request);
        
        TenantContext.setTenant(schemaName);
        
        log.info("Registering bank admin for tenant: {}", schemaName);
        bankAdminRegistrationService.registerAdmin(request);
        
        log.info("Tenant {} registered successfully", schemaName);
    }

    private String generateSchemaName(String tenantName) {
        String clean = tenantName.toLowerCase()
                .replaceAll("[^a-z0-9]", "_");

        return "tenant_" + clean;
    }
}