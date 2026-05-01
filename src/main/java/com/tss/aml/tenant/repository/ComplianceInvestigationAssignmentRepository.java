package com.tss.aml.tenant.repository;

import com.tss.aml.tenant.entity.ComplianceInvestigationAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ComplianceInvestigationAssignmentRepository extends JpaRepository<ComplianceInvestigationAssignment, UUID> {
    Boolean existsByCustomerCustomerNumberAndIsOpenTrue(String customerNumber);

    Optional<ComplianceInvestigationAssignment> findByCustomerCustomerNumberAndIsOpenTrue(String customerNumber);

    Page<ComplianceInvestigationAssignment> findByComplianceOfficerId(UUID investigationAssignment, Pageable pageable);
}
