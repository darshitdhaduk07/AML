package com.tss.aml.service;

import com.tss.aml.dto.request.CaseRequestDto;
import com.tss.aml.dto.request.ComplianceInvestigationAssignmentDto;
import com.tss.aml.dto.result.ComplianceInvestigationAssignmentResponseDto;
import com.tss.aml.dto.result.CustomerResponseDto;
import com.tss.aml.enums.CaseStatus;
import com.tss.aml.enums.NotificationType;
import com.tss.aml.exception.ResourceNotFoundException;
import com.tss.aml.mapper.CustomerResponseDtoMapper;
import com.tss.aml.tenant.entity.BrokenRule;
import com.tss.aml.tenant.entity.Case;
import com.tss.aml.tenant.entity.ComplianceInvestigationAssignment;
import com.tss.aml.tenant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvestigationService {

    private final ComplianceInvestigationAssignmentRepository complianceInvestigationAssignmentRepository;
    private final ComplianceOfficerRepository complianceOfficerRepository;
    private final CustomerRepository customerRepository;
    private final CaseRepository caseRepository;
    private final BrokenRuleRepository brokenRuleRepository;
    private final AppUserService appUserService;
    private final CustomerResponseDtoMapper customerResponseDtoMapper;
    private final InAppNotificationService inAppNotificationService;
    private final BankAdminRepository bankAdminRepository;

    public void assignComplianceOfficer(ComplianceInvestigationAssignmentDto request) {
        if (complianceInvestigationAssignmentRepository.existsByCustomerCustomerNumberAndIsOpenTrue(request.getCustomerNumber())) {
            throw new IllegalStateException("Customer investigation is already assigned to someone");
        }

        ComplianceInvestigationAssignment data = new ComplianceInvestigationAssignment();
        data.setComplianceOfficer(complianceOfficerRepository
                .findByEmail(request.getComplianceOfficerEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Compliance Officer", request.getComplianceOfficerEmail())
                )
        );

        data.setCustomer(customerRepository
                .findByCustomerNumber(request.getCustomerNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer", request.getCustomerNumber())
                )
        );

        complianceInvestigationAssignmentRepository.save(data);

        // Notify Compliance Officer
        inAppNotificationService.createNotification(
                data.getComplianceOfficer().getEmail(),
                com.tss.aml.enums.Role.COMPLIANCE_OFFICER,
                NotificationType.CASE_ASSIGNED,
                "A new investigation for customer " + request.getCustomerNumber() + " has been assigned to you."
        );
    }

    public void createCase(CaseRequestDto request) {
        Case data = new Case();
        data.setCaseDescription(request.getCaseDescription());
        data.setCaseName(request.getCaseName());
        data.setCaseStatus(CaseStatus.OPEN);
        data.setInvestigatedCustomer(complianceInvestigationAssignmentRepository
                .findByCustomerCustomerNumberAndIsOpenTrue(request.getCustomerNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer Investigation", request.getCustomerNumber())
                )
        );

        caseRepository.save(data);
    }

    public void markFalsePositive(UUID brokenRuleId) {
        BrokenRule brokenRule = brokenRuleRepository
                .findById(brokenRuleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Broken Rule", brokenRuleId)
                );

        ComplianceInvestigationAssignment assignment = complianceInvestigationAssignmentRepository
                .findByCustomerCustomerNumberAndIsOpenTrue(brokenRule.getCustomer().getCustomerNumber())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer Investigation", brokenRule.getCustomer().getCustomerNumber())
                );

        brokenRule.setFalsePositive(true);

        brokenRuleRepository.save(brokenRule);
    }

    public List<ComplianceInvestigationAssignmentResponseDto> getAssignments() {
        List<ComplianceInvestigationAssignment> assignments =
                complianceInvestigationAssignmentRepository
                        .findByComplianceOfficerId
                                (
                                        complianceOfficerRepository
                                                .findByEmail(appUserService.get().getUsername())
                                                .orElseThrow(() ->
                                                        new ResourceNotFoundException("Investigation Assignment", appUserService.get().getUsername())
                                                )
                                                .getId()
                                );

        return assignments
                .stream()
                .map(I -> {
                            ComplianceInvestigationAssignmentResponseDto dto = new ComplianceInvestigationAssignmentResponseDto();
                            dto.setCustomerResponseDto(customerResponseDtoMapper.mapCustomer(I.getCustomer()));
                            dto.setRiskScore(I.getRiskScore());
                            dto.setIsOpen(I.getIsOpen());

                            return dto;
                        }
                )
                .toList();
    }

    public void escalateCase(UUID caseId) {
        Case caseData = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", caseId));
        caseData.setCaseStatus(CaseStatus.ESCALATED);
        caseRepository.save(caseData);

        // Notify Bank Admin
        notifyBankAdmin(NotificationType.CASE_ESCALATED, "Case " + caseData.getCaseName() + " has been escalated.");
    }

    public void fileSAR(UUID caseId) {
        Case caseData = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", caseId));
        caseData.setSarFiled(true);
        caseRepository.save(caseData);

        // Notify Bank Admin
        notifyBankAdmin(NotificationType.SAR_FILED, "SAR/STR has been filed for case " + caseData.getCaseName() + ".");
    }

    private void notifyBankAdmin(NotificationType type, String message) {
        bankAdminRepository.findAll().stream().findFirst().ifPresent(admin -> 
            inAppNotificationService.createNotification(admin.getEmail(), com.tss.aml.enums.Role.BANK_ADMIN, type, message)
        );
    }
}