package com.tss.aml.service;

import com.tss.aml.dto.request.SelectedRuleRegisterDto;
import com.tss.aml.dto.result.AlertResponseDto;
import com.tss.aml.dto.result.SelectedRuleResponseDto;
import com.tss.aml.exception.ParameterMismatchException;
import com.tss.aml.rule_engine.rule_template.IRuleTemplate;
import com.tss.aml.rule_engine.RuleTemplateFactory;
import com.tss.aml.tenant.entity.SelectedRule;
import com.tss.aml.tenant.repository.BrokenRuleRepository;
import com.tss.aml.tenant.repository.SelectedRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleService {
    private final SelectedRuleRepository selectedRuleRepository;
    private final RuleTemplateFactory ruleTemplateFactory;
    private final BrokenRuleRepository brokenRuleRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registerRule(SelectedRuleRegisterDto rule) {
        IRuleTemplate ruleTemplate = ruleTemplateFactory.getRuleTemplate(rule.getRuleCode());

        if(!ruleTemplate.validateParameters(rule.getParameters(), ruleTemplate.getRequiredParameters())){
            throw new ParameterMismatchException();
        }

        SelectedRule selectedRule = new SelectedRule();
        selectedRule.setRuleCode(rule.getRuleCode());
        selectedRule.setWeight(rule.getWeight());
        selectedRule.setDescription(rule.getDescription());
        selectedRule.setParameters(rule.getParameters());

        selectedRuleRepository.save(selectedRule);

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<SelectedRuleResponseDto> getSelectedRules() {
        return selectedRuleRepository
                .findAll()
                .stream()
                .map(sr -> {
                    SelectedRuleResponseDto dto = new SelectedRuleResponseDto();
                    dto.setDescription(sr.getDescription());
                    dto.setRuleCode(sr.getRuleCode());
                    dto.setWeight(sr.getWeight());
                    dto.setParameters(sr.getParameters());

                    return dto;
                })
                .toList();
    }

    public List<AlertResponseDto> getAlerts() {
        return brokenRuleRepository
                .findAll()
                .stream()
                .map(br -> {
                    AlertResponseDto dto = new AlertResponseDto();
                    dto.setActive(br.getActive());
                    dto.setGroup_id(br.getGroup_id());
                    dto.setRuleDescription(br.getRule().getDescription());
                    dto.setCustomer_number(br.getCustomer().getCustomerNumber());
                    dto.setTransaction_number(br.getTransaction().getTransactionNumber());

                    return dto;
                })
                .toList();
    }
}