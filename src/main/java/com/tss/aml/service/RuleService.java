package com.tss.aml.service;

import com.tss.aml.dto.request.SelectedRuleRegisterDto;
import com.tss.aml.dto.result.AlertResponseDto;
import com.tss.aml.dto.result.CustomerResponseDto;
import com.tss.aml.dto.result.PaginatedResponseDto;
import com.tss.aml.dto.result.SelectedRuleResponseDto;
import com.tss.aml.exception.ParameterMismatchException;
import com.tss.aml.mapper.CustomerResponseDtoMapper;
import com.tss.aml.rule_engine.rule_template.IRuleTemplate;
import com.tss.aml.rule_engine.RuleTemplateFactory;
import com.tss.aml.tenant.entity.BrokenRule;
import com.tss.aml.tenant.entity.Customer;
import com.tss.aml.tenant.entity.SelectedRule;
import com.tss.aml.tenant.repository.BrokenRuleRepository;
import com.tss.aml.tenant.repository.SelectedRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RuleService {
    private final SelectedRuleRepository selectedRuleRepository;
    private final RuleTemplateFactory ruleTemplateFactory;
    private final BrokenRuleRepository brokenRuleRepository;
    private final CustomerResponseDtoMapper customerResponseDtoMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registerRule(SelectedRuleRegisterDto rule) {
        IRuleTemplate ruleTemplate = ruleTemplateFactory.getRuleTemplate(rule.getRuleCode());

        if(!ruleTemplate.validateParameters(rule.getParameters(), ruleTemplate.getRequiredParameters())) {
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
    public PaginatedResponseDto<SelectedRuleResponseDto> getSelectedRules(Pageable pageable) {
        Page<SelectedRule> selectedRulePage = selectedRuleRepository.findAll(pageable);
        
        List<SelectedRuleResponseDto> dtos = selectedRulePage.getContent().stream()
                .map(sr -> {
                    SelectedRuleResponseDto dto = new SelectedRuleResponseDto();
                    dto.setDescription(sr.getDescription());
                    dto.setRuleCode(sr.getRuleCode());
                    dto.setWeight(sr.getWeight());
                    dto.setSelectedRuleId(sr.getId());
                    dto.setParameters(sr.getParameters());
                    dto.setSelectedRuleId(sr.getId());

                    return dto;
                })
                .collect(Collectors.toList());

        return PaginatedResponseDto.<SelectedRuleResponseDto>builder()
                .content(dtos)
                .pageNumber(selectedRulePage.getNumber())
                .pageSize(selectedRulePage.getSize())
                .totalElements(selectedRulePage.getTotalElements())
                .totalPages(selectedRulePage.getTotalPages())
                .last(selectedRulePage.isLast())
                .build();
    }

    public PaginatedResponseDto<CustomerResponseDto> getAlerts(Pageable pageable) {
        Page<Customer> customerPage = brokenRuleRepository.findUniqueCustomersWithAlerts(pageable);
        
        List<CustomerResponseDto> dtos = customerPage.getContent().stream()
                .map(customerResponseDtoMapper::mapCustomer)
                .collect(Collectors.toList());

        return PaginatedResponseDto.<CustomerResponseDto>builder()
                .content(dtos)
                .pageNumber(customerPage.getNumber())
                .pageSize(customerPage.getSize())
                .totalElements(customerPage.getTotalElements())
                .totalPages(customerPage.getTotalPages())
                .last(customerPage.isLast())
                .build();
    }

    public List<AlertResponseDto> getAllAlertsSorted() {
        List<BrokenRule> brokenRules = brokenRuleRepository.findAllByOrderByTransactionTxnTimeDesc();
        
        return brokenRules.stream()
                .map(br -> {
                    AlertResponseDto dto = new AlertResponseDto();
                    dto.setActive(br.getActive());
                    dto.setGroup_id(br.getGroup_id());
                    dto.setRuleDescription(br.getRule().getDescription());
                    dto.setCustomer_number(br.getCustomer().getCustomerNumber());
                    dto.setTransaction(customerResponseDtoMapper.mapTransaction(br.getTransaction()));
                    dto.setRuleType(ruleTemplateFactory.getRuleTemplate(br.getRule().getRuleCode()).getRuleType());
                    dto.setWeight(br.getRule().getWeight());
                    dto.setRuleCode(br.getRule().getRuleCode());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<AlertResponseDto> getActiveAlertsForCustomer(String customerNumber) {
        List<BrokenRule> brokenRules = brokenRuleRepository.findByCustomerCustomerNumberAndActiveTrue(customerNumber);
        
        return brokenRules.stream()
                .map(customerResponseDtoMapper::mapAlert)
                .collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteSelectedRule(UUID selectedRuleId) {
        selectedRuleRepository.deleteById(selectedRuleId);
    }
}
