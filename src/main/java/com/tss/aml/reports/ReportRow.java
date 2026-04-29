package com.tss.aml.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReportRow {

    private String txnNumber;
    private String customerNumber;

    private String ruleCode;
    private Integer score;

    private LocalDateTime alertTime;
    private BigDecimal amount;
}