package com.tss.aml.dto.result;

import lombok.Data;

import java.util.Map;

@Data
public class SelectedRuleResponseDto {
    private String ruleCode;
    private Integer weight;
    private String description;
    private Map<String, Object> parameters;
}
