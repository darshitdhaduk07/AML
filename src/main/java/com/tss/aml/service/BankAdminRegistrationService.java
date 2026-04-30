package com.tss.aml.service;

import com.tss.aml.context.TenantContext;
import com.tss.aml.dto.request.TenantRegisterRequestDto;
import com.tss.aml.enums.NotificationType;
import com.tss.aml.tenant.entity.BankAdmin;
import com.tss.aml.tenant.repository.BankAdminRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankAdminRegistrationService {

    private final BankAdminRepository bankAdminRepo;
    private final PasswordGenerator passwordGenerator;
    private final EntityManager entityManager;
    private final NotificationService notificationService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registerAdmin(TenantRegisterRequestDto tenantRegisterRequestDto){

        String email = tenantRegisterRequestDto.getEmail();
        String tenant = TenantContext.getTenant();

        log.info("Starting BankAdmin registration | email={} | tenant={}", email, tenant);

        BankAdmin bankAdmin = new BankAdmin();
        String pass = passwordGenerator.generateStrong();

        bankAdmin.setEmail(email);
        bankAdmin.setPassword(pass);

        Object schema = entityManager
                .createNativeQuery("select current_schema()")
                .getSingleResult();

        log.debug("Resolved DB schema={} for tenant={}", schema, tenant);

        bankAdminRepo.save(bankAdmin);

        log.info("BankAdmin saved successfully | email={} | tenant={}", email, tenant);

        try {
            notificationService.sendNotification(
                    email,
                    NotificationType.BANK_ADMIN_REGISTERED,
                    Map.of(
                            "email", email,
                            "password", pass,
                            "tenant", tenant
                    )
            );

            log.info("Registration email triggered | email={}", email);

        } catch (Exception e) {
            log.error("Failed to send registration email | email={} | tenant={}", email, tenant, e);
        }
    }
}