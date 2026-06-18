package com.tss.aml.reports;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CaseReportRow {

    private String customerNumber;
    private String customerName;

    private String txnNumber;
    private String ruleCode;
    private Integer score;

    private BigDecimal amount;
    private LocalDateTime alertTime;
}