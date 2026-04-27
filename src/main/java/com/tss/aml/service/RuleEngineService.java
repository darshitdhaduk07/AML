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
import com.tss.aml.tenant.repository.BrokenRuleRepository;
import com.tss.aml.tenant.repository.SelectedRuleRepository;
import com.tss.aml.tenant.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RuleEngineService {

    private final RuleTemplateFactory ruleTemplateFactory;
    private final TransactionRepository transactionRepository;
    private final SelectedRuleRepository selectedRuleRepository;
    private final BrokenRuleRepository brokenRuleRepository;

    @Transactional
    public void applyRules() {
        List<Transaction> unevaluatedTransactions = transactionRepository.findByEvaluatedFalse();
        List<SelectedRule> selectedRules = selectedRuleRepository.findAll();

        Set<Customer> unevaluatedCustomers = new HashSet<>();

        for(SelectedRule selectedRule: selectedRules){

            IRuleTemplate ruleTemplate = ruleTemplateFactory.getRuleTemplate(selectedRule.getRuleCode());

            if(ruleTemplate
                    .getRuleType()
                    .equals(RuleType.TRANSACTION)){

                for(Transaction transaction: unevaluatedTransactions){
                    boolean result = ((TransactionRuleTemplate)ruleTemplate)
                            .check(transaction, selectedRule.getParameters());

                    if(result){
                        BrokenRule brokenRule = new BrokenRule();
                        brokenRule.setRule(selectedRule);
                        brokenRule.setCustomer(transaction.getCustomer());
                        brokenRule.setActive(true);
                        brokenRule.setGroup_id(UUID.randomUUID());
                        brokenRule.setTransaction(transaction);

                        brokenRuleRepository.save(brokenRule);
                    }
                }
            }
        }

        unevaluatedTransactions.forEach(t->{
            t.setEvaluated(true);
            unevaluatedCustomers.add(t.getCustomer());
        });

        transactionRepository.saveAll(unevaluatedTransactions);

        for(SelectedRule selectedRule: selectedRules){
            IRuleTemplate ruleTemplate = ruleTemplateFactory.getRuleTemplate(selectedRule.getRuleCode());

            if(ruleTemplate
                    .getRuleType()
                    .equals(RuleType.CUSTOMER)){

                for(Customer customer: unevaluatedCustomers){
                    List<Transaction> result = ((CustomerRuleTemplate)ruleTemplate)
                            .check(customer, selectedRule.getParameters());

                    if(result != null){
                        UUID group_id = UUID.randomUUID();
                        for(Transaction transaction: result) {
                            BrokenRule brokenRule = new BrokenRule();
                            brokenRule.setRule(selectedRule);
                            brokenRule.setCustomer(transaction.getCustomer());
                            brokenRule.setActive(true);
                            brokenRule.setGroup_id(group_id);
                            brokenRule.setTransaction(transaction);

                            brokenRuleRepository.save(brokenRule);
                        }
                    }
                }
            }
        }
    }

}
