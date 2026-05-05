package com.tss.aml.rule_engine.rule_template;

import com.tss.aml.model.ParameterMeta;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RL_003 implements CustomerRuleTemplate {

    @Override
    public String getBulkInsertSql() {
        return """
                WITH account_summary AS (
                    SELECT 
                        account_number, 
                        customer_number,
                        gen_random_uuid() as gid
                    FROM transactions
                    WHERE txn_time > NOW() - (CAST(:duration AS text) || ' hours')::interval
                    AND evaluated = false
                    GROUP BY account_number, customer_number
                    HAVING SUM(CASE WHEN direction = 'DR' THEN amount ELSE 0 END) >= (SUM(CASE WHEN direction = 'CR' THEN amount ELSE 0 END) * CAST(:percentage AS decimal) / 100.0)
                    AND SUM(CASE WHEN direction = 'CR' THEN amount ELSE 0 END) > 0
                    AND SUM(CASE WHEN evaluated = false THEN 1 ELSE 0 END) > 0
                )
                INSERT INTO broken_rules (id, selected_rule_id, customer_number, transaction_id, active, group_id, created_at, updated_at, false_positive)
                SELECT gen_random_uuid(), :ruleId, t.customer_number, t.id, true, as_table.gid, NOW(), NOW(), false
                FROM transactions t
                JOIN account_summary as_table ON t.account_number = as_table.account_number
                WHERE t.txn_time > NOW() - (CAST(:duration AS text) || ' hours')::interval and evaluated = false
                """;
    }

    @Override
    public Map<String, ParameterMeta> getRequiredParameters() {
        return Map.of(
                "duration", new ParameterMeta(Integer.class, true, true),
                "percentage", new ParameterMeta(Integer.class, true, true)
        );
    }

    @Override
    public String getDescription() {
        return "Account flags as passthrough pipe (Mule) if debits > [Percentage]% of credits within [Duration] hours.";
    }
}
