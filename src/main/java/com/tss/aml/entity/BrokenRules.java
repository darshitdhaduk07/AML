package com.tss.aml.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "broken_rules")
public class BrokenRules {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID brokenRuleId;

    
    private UUID caseId;

    private UUID ruleId;
}
