package com.tss.aml.rule_engine.rule_template;

import com.tss.aml.tenant.entity.Transaction;
import tools.jackson.databind.JsonNode;

import java.math.BigDecimal;

//Transaction greater Than [Threshold]
public class RL_001 implements IRuleTemplate {
    @Override
    public boolean check(Transaction txn, JsonNode parameters) {
        BigDecimal threshold = parameters.get("threshold").asDecimal();
        return txn.getAmount().compareTo(threshold) > 0;
    }
}
