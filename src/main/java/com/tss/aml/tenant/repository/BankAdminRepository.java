package com.tss.aml.tenant.repository;

import com.tss.aml.tenant.entity.BankAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BankAdminRepository extends JpaRepository<BankAdmin, UUID> {
    Optional<BankAdmin> findByEmail(String email);
}
