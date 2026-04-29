package com.tss.aml.service;

import com.tss.aml.dto.request.ComplianceOfficerRegisterRequestDto;
import com.tss.aml.enums.NotificationType;
import com.tss.aml.tenant.entity.ComplianceOfficer;
import com.tss.aml.tenant.repository.ComplianceOfficerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplianceOfficerRegistrationService {

    private final ComplianceOfficerRepository repo;
    private final PasswordGenerator passwordGenerator;
    private final NotificationService notificationService;

    @Transactional
    public void registerCO(ComplianceOfficerRegisterRequestDto request) {

        String email = request.getEmail();

        log.info("Starting ComplianceOfficer registration | email={}", email);

        ComplianceOfficer complianceOfficer = new ComplianceOfficer();

        String password = passwordGenerator.generateStrong();

        complianceOfficer.setEmail(email);
        complianceOfficer.setPassword(password);

        repo.save(complianceOfficer);

        log.info("ComplianceOfficer saved successfully | email={}", email);

        notificationService.sendNotification(
                email,
                NotificationType.CO_REGISTERED,
                Map.of(
                        "email", email,
                        "password", password,
                        "role", "Compliance Officer"
                )
        );

    }
}