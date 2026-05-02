package com.tss.aml.rule_engine.rule_template;

import com.tss.aml.model.ParameterMeta;
import com.tss.aml.tenant.entity.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class RL_001 implements TransactionRuleTemplate {
    @Override
    public String getSqlCondition() {
        return "amount > :threshold";
    }

    @Override
    public Map<String, ParameterMeta> getRequiredParameters() {
        return Map.of(
                "threshold", new ParameterMeta(Integer.class, true, true)
        );
    }

    @Override
    public String getDescription() {
        return "Transaction Greater Than [Threshold].";
    }

}
