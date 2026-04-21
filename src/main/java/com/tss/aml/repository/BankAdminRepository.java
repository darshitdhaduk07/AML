package com.tss.aml.repository;

import com.tss.aml.entity.BankAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BankAdminRepository extends JpaRepository<BankAdmin, UUID> {
    Optional<BankAdmin> findByEmail(String email);
}
