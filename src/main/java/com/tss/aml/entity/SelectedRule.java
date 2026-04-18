package com.tss.aml.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "selected_rules")
public class SelectedRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID ruleId;

    private UUID ruleTemplateId;

    @Column(unique = true, nullable = false)
    private String ruleCode;

    private Integer weight;

    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> parameters;
}
