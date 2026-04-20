package com.tss.aml.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "selected_rules")
@Getter
@Setter
public class SelectedRule extends BaseEntity{



    @Column(unique = true, nullable = false)
    private String ruleCode;

    @Column
    private Integer weight;

    @Column
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> parameters;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_template_id")
    private RuleTemplate ruleTemplateId;

}
