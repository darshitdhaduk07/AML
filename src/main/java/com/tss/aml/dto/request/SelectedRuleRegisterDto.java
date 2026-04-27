package com.tss.aml.dto.request;

import lombok.Data;

import java.util.Map;

@Data
public class SelectedRuleRegisterDto {
    private String tenant;
    private String ruleCode;
    private Map<String, Object> parameters;
    private Integer weight;
    private String description;
}
