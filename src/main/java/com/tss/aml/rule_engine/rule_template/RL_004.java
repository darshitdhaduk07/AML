package com.tss.aml.rule_engine.rule_template;

import com.tss.aml.model.ParameterMeta;
import com.tss.aml.tenant.entity.Customer;
import com.tss.aml.tenant.entity.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RL_004 implements CustomerRuleTemplate {

    @Override
    public String getBulkInsertSql() {
        return """
                WITH unevaluated_customers AS (
                    SELECT customer_number, gen_random_uuid() AS gid
                    FROM transactions
                    WHERE evaluated = false
                    GROUP BY customer_number
                ), account_history AS (
                    SELECT 
                        t.id,
                        t.customer_number,
                        t.account_number,
                        t.txn_time,
                        t.amount,
                        t.evaluated,
                        LAG(t.txn_time, 1) OVER (PARTITION BY t.account_number ORDER BY t.txn_time) as prev_txn_time,
                        a.created_at as account_created_at
                    FROM transactions t
                    JOIN accounts a ON t.account_number = a.account_number
                    WHERE t.customer_number IN (SELECT customer_number FROM unevaluated_customers)
                )
                INSERT INTO broken_rules (id, selected_rule_id, customer_number, transaction_id, active, group_id, created_at, updated_at, false_positive)
                SELECT 
                    gen_random_uuid(), 
                    :ruleId, 
                    ah.customer_number, 
                    ah.id, 
                    true, 
                    uc.gid, 
                    NOW(), 
                    NOW(), 
                    false
                FROM account_history ah
                JOIN unevaluated_customers uc ON ah.customer_number = uc.customer_number
                WHERE ah.evaluated = false
                  AND ah.amount > CAST(:threshold AS numeric)
                  AND EXTRACT(EPOCH FROM (ah.txn_time - COALESCE(ah.prev_txn_time, ah.account_created_at))) / 86400 > CAST(:duration AS numeric)
                """;
    }

    @Override
    public List<Transaction> check(Customer customer, Map<String, Object> parameters) {
        return null;
    }

    @Override
    public Map<String, ParameterMeta> getRequiredParameters() {
        return Map.of(
                "duration", new ParameterMeta(Integer.class, true, true),
                "threshold", new ParameterMeta(Integer.class, true, true)
        );
    }

    @Override
    public String getDescription() {
        return "The Lazarus Account: Flags dormant accounts (inactive for > [duration] days) that suddenly execute a transfer exceeding [threshold].";
    }
}
