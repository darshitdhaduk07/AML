package com.tss.aml.tenant.repository;

import com.tss.aml.reports.ReportRow;
import com.tss.aml.tenant.entity.BrokenRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BrokenRuleRepository extends JpaRepository<BrokenRule, UUID> {
    @Query("""
    SELECT new com.tss.aml.reports.ReportRow(
        t.transactionNumber,
        c.customerNumber,
        r.ruleCode,
        r.weight,
        br.createdAt,
        t.amount
    )
    FROM BrokenRule br
    JOIN br.transaction t
    JOIN t.customer c
    JOIN br.rule r
    WHERE br.active = true AND br.createdAt BETWEEN :from AND :to 

""")
    List<ReportRow> getReportDataBetween(
            LocalDateTime from,
            LocalDateTime to
    );

    @Query("""
    SELECT new com.tss.aml.reports.ReportRow(
        t.transactionNumber,
        c.customerNumber,
        r.ruleCode,
        r.weight,
        br.createdAt,
        t.amount
    )
    FROM BrokenRule br
    JOIN br.transaction t
    JOIN t.customer c
    JOIN br.rule r
    WHERE br.active = true
""")
    List<ReportRow> getReportData();

    @Query("""
        SELECT br FROM BrokenRule br 
        WHERE br.active = true 
        AND NOT EXISTS (
            SELECT 1 FROM ComplianceInvestigationAssignment cia 
            WHERE cia.customer = br.customer AND cia.isOpen = true
        )
    """)
    Page<BrokenRule> findAlertsWithoutOpenInvestigation(Pageable pageable);

    @Query("""
        SELECT DISTINCT br.customer FROM BrokenRule br 
        WHERE br.active = true 
        AND NOT EXISTS (
            SELECT 1 FROM ComplianceInvestigationAssignment cia 
            WHERE cia.customer = br.customer AND cia.isOpen = true
        )
    """)
    Page<com.tss.aml.tenant.entity.Customer> findUniqueCustomersWithAlerts(Pageable pageable);

    Page<BrokenRule> findByActiveTrue(Pageable pageable);

    List<BrokenRule> findByCustomerCustomerNumberAndActiveTrue(String customerNumber);

    List<BrokenRule> findAllByOrderByTransactionTxnTimeDesc();

    @Query("SELECT SUM(br.rule.weight) FROM BrokenRule br WHERE br.customer.customerNumber = :customerNumber AND br.active = true AND br.falsePositive = false")
    Integer calculateRiskScoreByCustomerNumber(String customerNumber);
}
