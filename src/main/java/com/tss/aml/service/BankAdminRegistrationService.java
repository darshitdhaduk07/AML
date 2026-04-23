package com.tss.aml.service;

import com.tss.aml.dto.request.RegisterRequestDto;
import com.tss.aml.tenant.entity.BankAdmin;
import com.tss.aml.tenant.repository.BankAdminRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BankAdminRegistrationService {
    private final BankAdminRepository bankAdminRepo;
    private final PasswordGenerator passwordGenerator;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registerAdmin(RegisterRequestDto registerRequestDto){
        BankAdmin bankAdmin = new BankAdmin();
        bankAdmin.setEmail(registerRequestDto.getEmail());
        bankAdmin.setPassword(passwordGenerator.generateStrong());

        //send Email

        bankAdminRepo.save(bankAdmin);
    }

}
