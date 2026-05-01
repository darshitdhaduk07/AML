package com.tss.aml.tenant.repository;

import com.tss.aml.reports.CaseReportRow;
import com.tss.aml.reports.ReportRow;
import com.tss.aml.tenant.entity.BrokenRule;
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

    org.springframework.data.domain.Page<BrokenRule> findByActiveTrue(org.springframework.data.domain.Pageable pageable);

    List<BrokenRule> findByCustomerCustomerNumberAndActiveTrue(String customerNumber);
}
