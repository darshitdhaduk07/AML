package com.tss.aml.repository;

import com.tss.aml.entity.ComplianceOfficer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ComplianceOfficerRepository extends JpaRepository<ComplianceOfficer, UUID> {
    Optional<ComplianceOfficer> findByEmail(String email);
}
