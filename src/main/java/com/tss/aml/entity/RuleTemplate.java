package com.tss.aml.entity;

import jakarta.persistence.Column;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "rule_templates")
@Getter
@Setter
public class RuleTemplate extends BaseEntity{

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parameters", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> parameters;

    @OneToOne(mappedBy = "ruleTemplateId",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private SelectedRule selectedRule;
}
