package com.tss.aml.entity;


import com.tss.aml.enums.CaseStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cases")
@Getter
@Setter
public class Case extends BaseEntity{

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

}
