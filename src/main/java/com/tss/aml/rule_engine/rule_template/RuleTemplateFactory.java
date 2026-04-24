package com.tss.aml.rule_engine.rule_template;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RuleTemplateFactory {
    private final Map<String, IRuleTemplate> ruleTemplateMap;

    public RuleTemplateFactory(List<IRuleTemplate> ruleTemplates) {
        this.ruleTemplateMap = ruleTemplates.stream()
                .collect(Collectors.toMap(s->s.getClass().getSimpleName(), s->s));
    }

    public IRuleTemplate getRuleTemplate(String ruleTemplateName) {
        return ruleTemplateMap.get(ruleTemplateName);
    }

    public Set<String> getRuleTemplates(){
        return ruleTemplateMap.keySet();
    }
}
