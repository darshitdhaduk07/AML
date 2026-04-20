package com.tss.aml.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "broken_rules")
@Getter
@Setter
public class BrokenRules extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID brokenRuleId;

    
    private UUID caseId;

    private UUID ruleId;
}
