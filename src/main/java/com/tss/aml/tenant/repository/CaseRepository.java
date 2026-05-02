package com.tss.aml.tenant.repository;

import com.tss.aml.enums.CaseStatus;
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

    org.springframework.data.domain.Page<Case> findByCaseStatus(CaseStatus status, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Case c " +
            "WHERE c.investigatedCustomer.customer.customerNumber = :customerNumber " +
            "AND c.investigatedCustomer.isOpen = true")
    boolean existsByInvestigatedCustomerCustomerAndInvestigatedCustomerIsOpenTrue(@org.springframework.data.repository.query.Param("customerNumber") String customerNumber);

    @Query("SELECT c FROM Case c WHERE c.isSarFiled = true")
    List<Case> findBySarFiledTrue();
}
