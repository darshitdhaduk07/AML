package com.tss.aml.rule_engine.rule_template;

import com.tss.aml.tenant.entity.Transaction;
import tools.jackson.databind.JsonNode;

import java.math.BigDecimal;

//Per [Duration] Day/s Transaction Frequency [Greater] than [threshold]
public class RL_002 implements IRuleTemplate {
    @Override
    public boolean check(Transaction txn, JsonNode parameters) {
        return false;
    }
}