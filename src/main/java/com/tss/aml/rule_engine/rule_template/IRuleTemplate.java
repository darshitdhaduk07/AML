package com.tss.aml.rule_engine.rule_template;

import com.tss.aml.enums.RuleType;
import com.tss.aml.model.ParameterMeta;

import java.util.Map;

public interface IRuleTemplate {
    RuleType getRuleType();

    Map<String, ParameterMeta> getRequiredParameters();

    default boolean validateParameters(Map<String, Object> input,
                                      Map<String, ParameterMeta> metaMap) {

        for (Map.Entry<String, ParameterMeta> entry : metaMap.entrySet()) {

            String key = entry.getKey();
            ParameterMeta meta = entry.getValue();

            if (!meta.validate(input.get(key))) {
                return false;
            }
        }

        return true;
    }

    String getDescription();
}
