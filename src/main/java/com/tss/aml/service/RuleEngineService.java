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
        long unevaluatedCount = transactionRepository.countByEvaluatedFalse();
        if (unevaluatedCount == 0) {
            log.info("No unevaluated transactions found");
            return;
        }

        List<SelectedRule> selectedRules = selectedRuleRepository.findAll();

        // 1. Process CUSTOMER rules first
        for (SelectedRule selectedRule : selectedRules) {
            IRuleTemplate ruleTemplate = ruleTemplateFactory.getRuleTemplate(selectedRule.getRuleCode());

            if (ruleTemplate.getRuleType().equals(RuleType.CUSTOMER)) {
                String bulkSql = ((CustomerRuleTemplate) ruleTemplate).getBulkInsertSql();

                if (bulkSql != null) {
                    Query query = entityManager.createNativeQuery(bulkSql);
                    query.setParameter("ruleId", selectedRule.getId());

                    if (selectedRule.getParameters() != null) {
                        selectedRule.getParameters().forEach(query::setParameter);
                    }

                    int rowsAffected = query.executeUpdate();
                    log.info("Customer Rule {} applied (bulk): {} violations found", selectedRule.getRuleCode(), rowsAffected);
                }
            }
        }

        // 2. Process TRANSACTION rules next
        for (SelectedRule selectedRule : selectedRules) {
            IRuleTemplate ruleTemplate = ruleTemplateFactory.getRuleTemplate(selectedRule.getRuleCode());

            if (ruleTemplate.getRuleType().equals(RuleType.TRANSACTION)) {
                String condition = ((TransactionRuleTemplate) ruleTemplate).getSqlCondition(selectedRule.getParameters());

                String sql = "INSERT INTO broken_rules (id, selected_rule_id, customer_number, transaction_id, active, group_id, created_at, updated_at, false_positive) " +
                             "SELECT gen_random_uuid(), :ruleId, t.customer_number, t.id, true, gen_random_uuid(), NOW(), NOW(), false " +
                             "FROM transactions t " +
                             "WHERE t.evaluated = false AND " + condition;

                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("ruleId", selectedRule.getId());

                if (selectedRule.getParameters() != null) {
                    selectedRule.getParameters().forEach(query::setParameter);
                }

                int rowsAffected = query.executeUpdate();
                log.info("Transaction Rule {} applied: {} violations found", selectedRule.getRuleCode(), rowsAffected);
            }
        }

        // 3. Mark all processed transactions as evaluated
        int updatedCount = transactionRepository.markAllAsEvaluated();
        log.info("Rule application process completed. {} transactions marked as evaluated", updatedCount);
    }
}