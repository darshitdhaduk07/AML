package com.tss.aml.service;

import com.tss.aml.dto.request.ComplianceOfficerRegisterRequestDto;
import com.tss.aml.tenant.entity.ComplianceOfficer;
import com.tss.aml.tenant.repository.ComplianceOfficerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ComplianceOfficerRegistrationService {

    private final ComplianceOfficerRepository repo;
    private final PasswordGenerator passwordGenerator;

    @Transactional
    public void registerCO(ComplianceOfficerRegisterRequestDto request) {
        ComplianceOfficer complianceOfficer = new ComplianceOfficer();
        complianceOfficer.setEmail(request.getEmail());
        complianceOfficer.setPassword(passwordGenerator.generateStrong());

        //send Email

        repo.save(complianceOfficer);
    }
}
