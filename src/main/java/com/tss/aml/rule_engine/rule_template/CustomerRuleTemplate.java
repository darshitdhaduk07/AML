package com.tss.aml.rule_engine.rule_template;

import com.tss.aml.enums.RuleType;

public interface CustomerRuleTemplate extends IRuleTemplate {
    String getBulkInsertSql();

    default RuleType getRuleType(){
        return RuleType.CUSTOMER;
    }
}
