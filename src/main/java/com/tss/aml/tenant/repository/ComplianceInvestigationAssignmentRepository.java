package com.tss.aml.tenant.repository;

import com.tss.aml.tenant.entity.ComplianceInvestigationAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComplianceInvestigationAssignmentRepository extends JpaRepository<ComplianceInvestigationAssignment, UUID> {
    Boolean existsByCustomerCustomerNumberAndIsOpenTrue(String customerNumber);

    Optional<ComplianceInvestigationAssignment> findByCustomerCustomerNumberAndIsOpenTrue(String customerNumber);

    List<ComplianceInvestigationAssignment> findByComplianceOfficerId(UUID investigationAssignment);
}
