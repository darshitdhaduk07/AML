package com.tss.aml.entity;


import com.tss.aml.enums.CaseStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cases")
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID caseId;

    @NotNull
    private UUID transactionId;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer riskScore;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CaseStatus caseStatus;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
