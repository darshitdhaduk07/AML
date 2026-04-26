package com.tss.aml.rule_engine.rule_template;

import com.tss.aml.model.ParameterMeta;
import com.tss.aml.tenant.entity.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class RL_001 implements TransactionRuleTemplate {
    @Override
    public boolean check(Transaction txn, Map<String, Object> parameters) {
        long threshold = Long.parseLong(parameters.get("threshold").toString());
        return txn.getAmount().compareTo(new BigDecimal(threshold)) > 0;
    }

    @Override
    public Map<String, ParameterMeta> getRequiredParameters() {
        return Map.of(
                "threshold", new ParameterMeta(Double.class, true, true)
        );
    }

    @Override
    public String getDescription() {
        return "Transaction Greater Than [Threshold].";
    }

}
