package com.tss.aml.tenant.repository;

import com.tss.aml.tenant.entity.ComplianceInvestigationAssignment;
import com.tss.aml.tenant.entity.ComplianceOfficer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComplianceOfficerRepository extends JpaRepository<ComplianceOfficer, UUID> {
    Optional<ComplianceOfficer> findByEmail(String email);
}
