package com.tss.aml.service;

import com.tss.aml.enums.RuleType;
import com.tss.aml.rule_engine.rule_template.CustomerRuleTemplate;
import com.tss.aml.rule_engine.rule_template.IRuleTemplate;
import com.tss.aml.rule_engine.RuleTemplateFactory;
import com.tss.aml.rule_engine.rule_template.TransactionRuleTemplate;
import com.tss.aml.tenant.entity.BrokenRule;
import com.tss.aml.tenant.entity.Customer;
import com.tss.aml.tenant.entity.SelectedRule;
import com.tss.aml.tenant.entity.Transaction;
import com.tss.aml.tenant.repository.SelectedRuleRepository;
import com.tss.aml.tenant.repository.TransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RuleEngineService {

    private final RuleTemplateFactory ruleTemplateFactory;
    private final TransactionRepository transactionRepository;
    private final SelectedRuleRepository selectedRuleRepository;

    private final EntityManager entityManager;

    @Transactional
    public void applyRules() {
        List<Transaction> unevaluatedTransactions = transactionRepository.findByEvaluatedFalse();
        List<SelectedRule> selectedRules = selectedRuleRepository.findAll();

        Set<Customer> unevaluatedCustomers = new HashSet<>();
        List<BrokenRule> pendingBrokenRules = new ArrayList<>();

        for (SelectedRule selectedRule : selectedRules) {
            IRuleTemplate ruleTemplate = ruleTemplateFactory.getRuleTemplate(selectedRule.getRuleCode());

            if (ruleTemplate.getRuleType().equals(RuleType.TRANSACTION)) {
                for (Transaction transaction : unevaluatedTransactions) {
                    boolean result = ((TransactionRuleTemplate) ruleTemplate)
                            .check(transaction, selectedRule.getParameters());

                    if (result) {
                        BrokenRule brokenRule = new BrokenRule();
                        brokenRule.setId(UUID.randomUUID());
                        brokenRule.setRule(selectedRule);
                        brokenRule.setCustomer(transaction.getCustomer());
                        brokenRule.setActive(true);
                        brokenRule.setGroup_id(UUID.randomUUID());
                        brokenRule.setTransaction(transaction);

                        pendingBrokenRules.add(brokenRule);
                    }
                }
            }
        }

        unevaluatedTransactions.forEach(t -> unevaluatedCustomers.add(t.getCustomer()));

        for (SelectedRule selectedRule : selectedRules) {
            IRuleTemplate ruleTemplate = ruleTemplateFactory.getRuleTemplate(selectedRule.getRuleCode());

            if (ruleTemplate.getRuleType().equals(RuleType.CUSTOMER)) {
                for (Customer customer : unevaluatedCustomers) {
                    List<Transaction> result = ((CustomerRuleTemplate) ruleTemplate)
                            .check(customer, selectedRule.getParameters());

                    if (result != null) {
                        UUID group_id = UUID.randomUUID();
                        for (Transaction transaction : result) {
                            BrokenRule brokenRule = new BrokenRule();
                            brokenRule.setId(UUID.randomUUID());
                            brokenRule.setRule(selectedRule);
                            brokenRule.setCustomer(transaction.getCustomer());
                            brokenRule.setActive(true);
                            brokenRule.setGroup_id(group_id);
                            brokenRule.setTransaction(transaction);

                            pendingBrokenRules.add(brokenRule);
                        }
                    }
                }
            }
        }

        if (!pendingBrokenRules.isEmpty()) {
            bulkInsertBrokenRules(pendingBrokenRules);
        }

        if (!unevaluatedTransactions.isEmpty()) {
            bulkUpdateTransactionsAsEvaluated(unevaluatedTransactions);
        }
        log.info("Rule application process completed");
    }

    private void bulkInsertBrokenRules(List<BrokenRule> brokenRules) {
        int batchSize = 500;

        for (int i = 0; i < brokenRules.size(); i += batchSize) {
            List<BrokenRule> batch = brokenRules.subList(i, Math.min(i + batchSize, brokenRules.size()));

            StringBuilder sql = new StringBuilder("""
                    INSERT INTO broken_rules (
                        id, selected_rule_id, customer_number, transaction_id, active, group_id, created_at, updated_at
                    ) VALUES
                    """);

            List<Object> params = new ArrayList<>();

            for (int j = 0; j < batch.size(); j++) {
                BrokenRule br = batch.get(j);
                sql.append("(?, ?, ?, ?, ?, ?, NOW(), NOW())");

                if (j < batch.size() - 1) {
                    sql.append(", ");
                }

                params.add(br.getId());
                params.add(br.getRule().getId());
                params.add(br.getCustomer().getCustomerNumber());
                params.add(br.getTransaction().getId());
                params.add(br.getActive());
                params.add(br.getGroup_id());
            }

            Query query = entityManager.createNativeQuery(sql.toString());
            for (int k = 0; k < params.size(); k++) {
                query.setParameter(k + 1, params.get(k));
            }
            query.executeUpdate();

            entityManager.flush();
            entityManager.clear();
        }
    }

    private void bulkUpdateTransactionsAsEvaluated(List<Transaction> transactions) {
        int batchSize = 500;

        for (int i = 0; i < transactions.size(); i += batchSize) {
            List<Transaction> batch = transactions.subList(i, Math.min(i + batchSize, transactions.size()));

            List<UUID> transactionId = batch.stream()
                    .map(Transaction::getId)
                    .toList();

            String inClauseMarkers = transactionId.stream()
                    .map(t -> "?")
                    .collect(Collectors.joining(","));

            String sql = "UPDATE transactions SET evaluated = true, updated_at = NOW() WHERE id IN (" + inClauseMarkers + ")";

            Query query = entityManager.createNativeQuery(sql);
            for (int j = 0; j < transactionId.size(); j++) {
                query.setParameter(j + 1, transactionId.get(j));
            }
            query.executeUpdate();
        }
    }
}