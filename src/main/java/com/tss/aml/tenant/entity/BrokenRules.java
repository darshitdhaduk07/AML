package com.tss.aml.tenant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "broken_rules")
@Getter
@Setter
public class BrokenRules extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_rule_id", nullable = false)
    private SelectedRule rule;
}
