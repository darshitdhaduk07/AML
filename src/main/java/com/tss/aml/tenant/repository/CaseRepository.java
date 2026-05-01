package com.tss.aml.tenant.repository;

import com.tss.aml.reports.CaseReportRow;
import com.tss.aml.tenant.entity.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CaseRepository extends JpaRepository<Case, UUID> {
    @Query("""
    SELECT new com.tss.aml.reports.CaseReportRow(
        c.customerNumber,
        CONCAT(c.firstName, ' ', c.lastName),
        t.transactionNumber,
        r.ruleCode,
        r.weight,
        t.amount,
        br.createdAt
    )
    FROM BrokenRule br
    JOIN br.transaction t
    JOIN t.customer c
    JOIN br.rule r
    WHERE br.active = true
    AND c.customerNumber = :customerNumber
""")
    List<CaseReportRow> getCaseDataByCustomer(String customerNumber);

    org.springframework.data.domain.Page<Case> findByCaseStatus(com.tss.aml.enums.CaseStatus status, org.springframework.data.domain.Pageable pageable);
}
