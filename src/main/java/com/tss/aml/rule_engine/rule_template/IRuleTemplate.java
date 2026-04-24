package com.tss.aml.rule_engine.rule_template;

import com.tss.aml.tenant.entity.Transaction;
import tools.jackson.databind.JsonNode;

public interface IRuleTemplate {
    boolean check(Transaction txn, JsonNode parameters);
}
