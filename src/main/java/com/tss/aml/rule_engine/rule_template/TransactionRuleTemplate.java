package com.tss.aml.rule_engine.rule_template;

import com.tss.aml.enums.RuleType;
import com.tss.aml.tenant.entity.Transaction;
import java.util.Map;

public interface TransactionRuleTemplate extends IRuleTemplate {
    boolean check(Transaction txn, Map<String, Object> parameters);

    default RuleType getRuleType(){
        return RuleType.TRANSACTION;
    }


}
