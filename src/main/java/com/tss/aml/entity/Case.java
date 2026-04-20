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

    @Column(nullable = false)
    private String caseName;

    @Column(length = 1000)
    private String caseDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseStatus caseStatus;

}
