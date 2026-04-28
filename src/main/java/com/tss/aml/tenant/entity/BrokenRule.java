package com.tss.aml.tenant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "broken_rules")
@Getter
@Setter
public class BrokenRule extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_number", referencedColumnName = "customerNumber", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_rule_id", nullable = false)
    private SelectedRule rule;

    @Column
    private UUID group_id;

    @Column
    private Boolean falsePositive;

    @Column
    private Boolean active;

    @PrePersist
    public void prePersist() {
        this.active = true;
        this.falsePositive = false;
    }
}
