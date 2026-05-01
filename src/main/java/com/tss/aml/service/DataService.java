package com.tss.aml.service;

import com.tss.aml.dto.result.ComplianceOfficerResponseDto;
import com.tss.aml.dto.result.RuleTemplateResponseDto;
import com.tss.aml.dto.result.TenantResponseDto;
import com.tss.aml.enums.RuleType;
import com.tss.aml.master.repository.TenantRepository;
import com.tss.aml.rule_engine.RuleTemplateFactory;
import com.tss.aml.rule_engine.rule_template.IRuleTemplate;
import com.tss.aml.tenant.repository.ComplianceOfficerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DataService {

    private final TenantRepository tenantRepository;
    private final RuleTemplateFactory ruleTemplateFactory;
    private final ComplianceOfficerRepository complianceOfficerRepository;

    public List<TenantResponseDto> getTenants() {
        return tenantRepository
                .findAll()
                .stream()
                .map(t -> {
                    TenantResponseDto dto = new TenantResponseDto();
                    dto.setTenantName(t.getTenantName());
                    dto.setTenantStatus(t.getTenantStatus());
                    return dto;
                })
                .toList();
    }

    public List<RuleTemplateResponseDto> getRuleTemplates() {
        Set<IRuleTemplate> ruleTemplates = ruleTemplateFactory
                .getRuleTemplatesByType(RuleType.TRANSACTION);

        ruleTemplates
                .addAll(
                ruleTemplateFactory
                        .getRuleTemplatesByType(RuleType.CUSTOMER)
                );

        return ruleTemplates
                .stream()
                .map(rt ->{
                    RuleTemplateResponseDto dto = new RuleTemplateResponseDto();
                    dto.setRuleTemplateCode(rt.getClass().getSimpleName());
                    dto.setRuleTemplateType(rt.getRuleType());
                    dto.setRuleTemplateDescription(rt.getDescription());
                    dto.setRequiredParameters(rt.getRequiredParameters());

                    return dto;
                })
                .toList();
    }

    public List<ComplianceOfficerResponseDto> getComplianceOfficers() {
        return complianceOfficerRepository
                .findAll()
                .stream()
                .map(co -> {
                    ComplianceOfficerResponseDto dto = new ComplianceOfficerResponseDto();
                    dto.setEmail(co.getEmail());
                    dto.setIsSuspended(co.isSuspended());
                    dto.setIsLocked(co.isLocked());

                    return dto;
                })
                .toList();
    }
}
