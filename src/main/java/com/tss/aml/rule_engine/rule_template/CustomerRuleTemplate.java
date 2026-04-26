package com.tss.aml.rule_engine.rule_template;

import com.tss.aml.enums.RuleType;
import com.tss.aml.tenant.entity.Customer;

import java.util.Map;

public interface CustomerRuleTemplate extends IRuleTemplate {
    boolean check(Customer customer, Map<String, Object> parameters);

    default RuleType getRuleType(){
        return RuleType.CUSTOMER;
    }
}
