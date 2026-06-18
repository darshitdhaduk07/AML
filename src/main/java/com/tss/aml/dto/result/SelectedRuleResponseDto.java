package com.tss.aml.dto.result;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class SelectedRuleResponseDto {
    private String ruleCode;
    private Integer weight;
    private String description;
    private UUID selectedRuleId;
    private Map<String, Object> parameters;
}
