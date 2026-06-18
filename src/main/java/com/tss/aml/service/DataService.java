package com.tss.aml.service;

import com.tss.aml.dto.result.ComplianceOfficerResponseDto;
import com.tss.aml.dto.result.PaginatedResponseDto;
import com.tss.aml.dto.result.RuleTemplateResponseDto;
import com.tss.aml.dto.result.TenantResponseDto;
import com.tss.aml.enums.RuleType;
import com.tss.aml.master.entity.Tenant;
import com.tss.aml.master.repository.TenantRepository;
import com.tss.aml.rule_engine.RuleTemplateFactory;
import com.tss.aml.rule_engine.rule_template.IRuleTemplate;
import com.tss.aml.tenant.entity.ComplianceOfficer;
import com.tss.aml.tenant.repository.ComplianceOfficerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataService {

    private final TenantRepository tenantRepository;
    private final RuleTemplateFactory ruleTemplateFactory;
    private final ComplianceOfficerRepository complianceOfficerRepository;

    public PaginatedResponseDto<TenantResponseDto> getTenants(Pageable pageable) {
        Page<Tenant> tenantPage = tenantRepository.findAll(pageable);
        
        List<TenantResponseDto> dtos = tenantPage.getContent().stream()
                .map(t -> {
                    TenantResponseDto dto = new TenantResponseDto();
                    dto.setTenantName(t.getTenantName());
                    dto.setTenantStatus(t.getTenantStatus());
                    return dto;
                })
                .collect(Collectors.toList());

        return PaginatedResponseDto.<TenantResponseDto>builder()
                .content(dtos)
                .pageNumber(tenantPage.getNumber())
                .pageSize(tenantPage.getSize())
                .totalElements(tenantPage.getTotalElements())
                .totalPages(tenantPage.getTotalPages())
                .last(tenantPage.isLast())
                .build();
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

    public PaginatedResponseDto<ComplianceOfficerResponseDto> getComplianceOfficers(Pageable pageable) {
        Page<ComplianceOfficer> complianceOfficerPage = complianceOfficerRepository.findAll(pageable);
        
        List<ComplianceOfficerResponseDto> dtos = complianceOfficerPage.getContent().stream()
                .map(co -> {
                    ComplianceOfficerResponseDto dto = new ComplianceOfficerResponseDto();
                    dto.setEmail(co.getEmail());
                    dto.setIsSuspended(co.isSuspended());
                    dto.setIsLocked(co.isLocked());

                    return dto;
                })
                .collect(Collectors.toList());

        return PaginatedResponseDto.<ComplianceOfficerResponseDto>builder()
                .content(dtos)
                .pageNumber(complianceOfficerPage.getNumber())
                .pageSize(complianceOfficerPage.getSize())
                .totalElements(complianceOfficerPage.getTotalElements())
                .totalPages(complianceOfficerPage.getTotalPages())
                .last(complianceOfficerPage.isLast())
                .build();
    }
}
