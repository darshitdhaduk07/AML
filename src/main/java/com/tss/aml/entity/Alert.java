package com.tss.aml.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

public class Alert extends BaseEntity{

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_number",unique = true)
    private Transaction transaction;

    private BigDecimal riskScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id",unique=true)
    private Case caseId;
}
