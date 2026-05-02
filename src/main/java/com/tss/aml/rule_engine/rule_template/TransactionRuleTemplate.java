package com.tss.aml.rule_engine.rule_template;

import com.tss.aml.enums.RuleType;

public interface TransactionRuleTemplate extends IRuleTemplate {
    String getSqlCondition();

    default RuleType getRuleType(){
        return RuleType.TRANSACTION;
    }
}
