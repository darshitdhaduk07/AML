package com.tss.aml.tenant.entity;


import com.tss.aml.enums.CaseStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investigated_customer", nullable = false)
    ComplianceInvestigationAssignment investigatedCustomer;

}
