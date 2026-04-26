package com.tss.aml.rule_engine;

import com.tss.aml.enums.RuleType;
import com.tss.aml.exception.ResourceNotFoundException;
import com.tss.aml.rule_engine.rule_template.IRuleTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RuleTemplateFactory {
    private final Map<RuleType, Map<String, IRuleTemplate>> ruleTemplates;

    public RuleTemplateFactory(List<IRuleTemplate> ruleTemplates) {
        this.ruleTemplates = new HashMap<>();
        Map<String, IRuleTemplate> transactionRuleTemplates = new HashMap<>();
        Map<String, IRuleTemplate> customerRuleTemplates = new HashMap<>();

        ruleTemplates.forEach(ruleTemplate -> {
            if(ruleTemplate.getRuleType() == RuleType.CUSTOMER){
                customerRuleTemplates.put(ruleTemplate.getClass().getSimpleName(), ruleTemplate);
            }
            else{
                transactionRuleTemplates.put(ruleTemplate.getClass().getSimpleName(), ruleTemplate);
            }
        });

        this.ruleTemplates.put(RuleType.CUSTOMER, customerRuleTemplates);
        this.ruleTemplates.put(RuleType.TRANSACTION, transactionRuleTemplates);

    }

    public IRuleTemplate getRuleTemplate(String ruleTemplateCode) {
        if(ruleTemplates.get(RuleType.CUSTOMER).containsKey(ruleTemplateCode)){
            return ruleTemplates.get(RuleType.CUSTOMER).get(ruleTemplateCode);
        }
        else if(ruleTemplates.get(RuleType.TRANSACTION).containsKey(ruleTemplateCode)){
            return ruleTemplates.get(RuleType.TRANSACTION).get(ruleTemplateCode);
        }

        throw new ResourceNotFoundException("ruleTemplate", ruleTemplateCode);
    }

    public Set<IRuleTemplate> getRuleTemplatesByType(RuleType type){
        return new HashSet<>(ruleTemplates.get(type).values());
    }
}
