package com.tss.aml.tenant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "alerts")
@Getter
@Setter
public class Alert extends BaseEntity{

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_number",unique = true)
    private Transaction transaction;

    @Column
    private BigDecimal riskScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id",unique=true)
    private Case assignedCase;
}
