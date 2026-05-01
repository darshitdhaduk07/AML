package com.tss.aml.rule_engine.rule_template;

import com.tss.aml.model.ParameterMeta;
import com.tss.aml.tenant.entity.Customer;
import com.tss.aml.tenant.entity.Transaction;
import com.tss.aml.tenant.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RL_002 implements CustomerRuleTemplate {

    private final TransactionRepository transactionRepository;

    @Override
    public String getBulkInsertSql() {
        return """
                WITH violating_customers AS (
                    SELECT customer_number, gen_random_uuid() as gid
                    FROM transactions
                    WHERE txn_time > NOW() - (CAST(:duration AS text) || ' days')::interval
                    GROUP BY customer_number
                    HAVING COUNT(*) > :threshold
                    AND SUM(CASE WHEN evaluated = false THEN 1 ELSE 0 END) > 0
                )
                INSERT INTO broken_rules (id, selected_rule_id, customer_number, transaction_id, active, group_id, created_at, updated_at, false_positive)
                SELECT gen_random_uuid(), :ruleId, t.customer_number, t.id, true, vc.gid, NOW(), NOW(), false
                FROM transactions t
                JOIN violating_customers vc ON t.customer_number = vc.customer_number
                WHERE t.txn_time > NOW() - (CAST(:duration AS text) || ' days')::interval
                """;
    }

    @Override
    public List<Transaction> check(Customer customer, Map<String, Object> parameters) {

        int durationDays = (int) parameters.get("duration");
        int threshold = (int) parameters.get("threshold");

        LocalDateTime fromTime = LocalDateTime.now().minusDays(durationDays);

        long count = transactionRepository.countByCustomerAndTxnTimeAfter(
                customer,
                fromTime
        );

        if(count <= threshold){
            return null;
        }

        return getViolatingTransactions(customer, fromTime);
    }

    private List<Transaction> getViolatingTransactions(Customer customer, LocalDateTime fromTime) {
        if (customer == null || fromTime == null) {
            return List.of();
        }

        return transactionRepository.findByCustomerAndTxnTimeAfter(
                customer,
                fromTime
        );
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
        return "Per [Duration] Day/s Transaction Frequency [Greater] than [threshold].";
    }
}