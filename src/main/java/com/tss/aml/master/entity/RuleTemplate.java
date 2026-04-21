package com.tss.aml.master.entity;

import com.tss.aml.tenant.entity.BaseEntity;
import com.tss.aml.tenant.entity.SelectedRule;
import jakarta.persistence.Column;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "rule_templates")
@Getter
@Setter
public class RuleTemplate extends BaseEntity {

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parameters", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> parameters;
}
