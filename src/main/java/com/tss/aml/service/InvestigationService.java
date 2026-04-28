package com.tss.aml.service;

import com.tss.aml.dto.request.CaseRequestDto;
import com.tss.aml.dto.request.ComplianceInvestigationAssignmentDto;
import com.tss.aml.enums.CaseStatus;
import com.tss.aml.exception.ResourceNotFoundException;
import com.tss.aml.tenant.entity.BrokenRule;
import com.tss.aml.tenant.entity.Case;
import com.tss.aml.tenant.entity.ComplianceInvestigationAssignment;
import com.tss.aml.tenant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvestigationService {

    private final ComplianceInvestigationAssignmentRepository complianceInvestigationAssignmentRepository;
    private final ComplianceOfficerRepository complianceOfficerRepository;
    private final CustomerRepository customerRepository;
    private final CaseRepository caseRepository;
    private final BrokenRuleRepository brokenRuleRepository;

    public void assignComplianceOfficer(ComplianceInvestigationAssignmentDto request){
        if(complianceInvestigationAssignmentRepository.existsByCustomerNumberAndIsOpenTrue(request.getCustomerNumber())){
            throw new IllegalStateException("Customer investigation is already assigned to someone");
        }

        ComplianceInvestigationAssignment data = new ComplianceInvestigationAssignment();
        data.setComplianceOfficer(complianceOfficerRepository
                .findById(request.getComplianceOfficerId())
                .orElseThrow(()->
                        new ResourceNotFoundException("Compliance Officer", request.getComplianceOfficerId())
                )
        );

        data.setCustomer(customerRepository
                .findByCustomerNumber(request.getCustomerNumber())
                .orElseThrow(()->
                        new ResourceNotFoundException("Customer", request.getCustomerNumber())
                )
        );

        complianceInvestigationAssignmentRepository.save(data);
    }

    public void createCase(CaseRequestDto request) {
        Case data = new Case();
        data.setCaseDescription(request.getCaseDescription());
        data.setCaseName(request.getCaseName());
        data.setCaseStatus(CaseStatus.OPEN);
        data.setInvestigatedCustomer(complianceInvestigationAssignmentRepository
                .findByCustomerNumberAndIsOpenTrue(request.getCustomerNumber())
                .orElseThrow(()->
                        new ResourceNotFoundException("Customer Investigation", request.getCustomerNumber())
                )
        );

        caseRepository.save(data);
    }

    public void markFalsePositive(UUID brokenRuleId) {
        BrokenRule brokenRule = brokenRuleRepository
                .findById(brokenRuleId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Broken Rule", brokenRuleId)
                );

        ComplianceInvestigationAssignment assignment = complianceInvestigationAssignmentRepository
                .findByCustomerNumberAndIsOpenTrue(brokenRule.getCustomer().getCustomerNumber())
                .orElseThrow(()->
                        new ResourceNotFoundException("Customer Investigation", brokenRule.getCustomer().getCustomerNumber())
                );

        brokenRule.setFalsePositive(true);

        brokenRuleRepository.save(brokenRule);
    }
}