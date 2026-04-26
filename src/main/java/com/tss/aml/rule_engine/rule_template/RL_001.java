package com.tss.aml.rule_engine.rule_template;

import com.tss.aml.tenant.entity.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

//Transaction greater Than [Threshold]
@Service
public class RL_001 implements TransactionRuleTemplate {
    @Override
    public boolean check(Transaction txn, Map<String, Object> parameters) {
        BigDecimal threshold = new BigDecimal((String)parameters.get("threshold"));
        return txn.getAmount().compareTo(threshold) > 0;
    }
}
