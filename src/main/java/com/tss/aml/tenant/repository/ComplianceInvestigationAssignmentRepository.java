package com.tss.aml.tenant.repository;

import com.tss.aml.tenant.entity.ComplianceInvestigationAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ComplianceInvestigationAssignmentRepository extends JpaRepository<ComplianceInvestigationAssignment, UUID> {
    Boolean existsByCustomerNumberAndIsOpenTrue(String customerNumber);

    Optional<ComplianceInvestigationAssignment> findByCustomerNumberAndIsOpenTrue(String customerNumber);
}
