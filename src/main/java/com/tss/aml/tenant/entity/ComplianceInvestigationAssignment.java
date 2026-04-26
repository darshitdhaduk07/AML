package com.tss.aml.tenant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "alerts")
@Getter
@Setter
public class ComplianceInvestigationAssignment extends BaseEntity{

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_number",referencedColumnName = "customerNumber")
    private Customer customer;

    @Column
    private BigDecimal riskScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compliance_officer_id")
    private ComplianceOfficer complianceOfficer;

    private Boolean isOpen;

    @PrePersist
    public void prePersist() {
        this.isOpen = false;
    }

}
