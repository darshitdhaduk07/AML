package com.tss.aml.service;

import com.tss.aml.dto.request.ComplianceOfficerRegisterRequestDto;
import com.tss.aml.enums.NotificationType;
import com.tss.aml.tenant.entity.ComplianceOfficer;
import com.tss.aml.tenant.repository.ComplianceOfficerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerCO(ComplianceOfficerRegisterRequestDto request, String password) {

        String email = request.getEmail();

        log.info("Starting ComplianceOfficer registration | email={}", email);

        ComplianceOfficer complianceOfficer = new ComplianceOfficer();

        complianceOfficer.setEmail(email);
        complianceOfficer.setPassword(passwordEncoder.encode(password));

        repo.save(complianceOfficer);

        log.info("ComplianceOfficer saved successfully | email={}", email);

    }
}