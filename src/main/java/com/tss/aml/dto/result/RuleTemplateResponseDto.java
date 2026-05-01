package com.tss.aml.dto.result;

import com.tss.aml.enums.RuleType;
import com.tss.aml.model.ParameterMeta;
import lombok.Data;

import java.util.Map;

@Data
public class RuleTemplateResponseDto {
    private RuleType ruleTemplateType;
    private String ruleTemplateDescription;
    private String ruleTemplateCode;
    private Map<String, ParameterMeta> requiredParameters;
}
