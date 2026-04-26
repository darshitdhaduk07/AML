package com.tss.aml.rule_engine.rule_template;

import com.tss.aml.enums.RuleType;
import com.tss.aml.tenant.entity.Customer;
import com.tss.aml.tenant.entity.Transaction;

import java.util.List;
import java.util.Map;

public interface CustomerRuleTemplate extends IRuleTemplate {
    List<Transaction> check(Customer customer, Map<String, Object> parameters);

    default RuleType getRuleType(){
        return RuleType.CUSTOMER;
    }

}
