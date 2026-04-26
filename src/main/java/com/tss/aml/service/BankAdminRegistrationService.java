package com.tss.aml.service;

import com.tss.aml.context.TenantContext;
import com.tss.aml.dto.request.TenantRegisterRequestDto;
import com.tss.aml.tenant.entity.BankAdmin;
import com.tss.aml.tenant.repository.BankAdminRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BankAdminRegistrationService {
    private final BankAdminRepository bankAdminRepo;
    private final PasswordGenerator passwordGenerator;
    private final EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registerAdmin(TenantRegisterRequestDto tenantRegisterRequestDto){

        BankAdmin bankAdmin = new BankAdmin();
        bankAdmin.setEmail(tenantRegisterRequestDto.getEmail());
        bankAdmin.setPassword(passwordGenerator.generateStrong());
        System.out.println("TenantContext = " + TenantContext.getTenant());
        //send Email

        Object schema = entityManager
                .createNativeQuery("select current_schema()")
                .getSingleResult();

        System.out.println("DB Schema = " + schema);

        bankAdminRepo.save(bankAdmin);
    }

}
