package com.tss.aml.rule_engine;

import com.tss.aml.rule_engine.rule_template.CustomerRuleTemplate;
import com.tss.aml.rule_engine.rule_template.RuleTemplateFactory;
import com.tss.aml.rule_engine.rule_template.TransactionRuleTemplate;
import com.tss.aml.tenant.entity.Customer;
import com.tss.aml.tenant.entity.SelectedRule;
import com.tss.aml.tenant.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RuleEvaluator {
    private final RuleTemplateFactory ruleTemplateFactory;

    public boolean evaluate(Transaction transaction, SelectedRule rule){
        return ( ( TransactionRuleTemplate ) ruleTemplateFactory
                .getRuleTemplate(rule.getRuleCode()))
                .check(transaction, rule.getParameters());
    }

    public boolean evaluate(Customer customer, SelectedRule rule){
        return ( (CustomerRuleTemplate) ruleTemplateFactory
                .getRuleTemplate(rule.getRuleCode()))
                .check(customer, rule.getParameters());
    }
}
