package com.tss.aml.service;

import com.tss.aml.dto.request.CaseRequestDto;
import com.tss.aml.dto.request.ComplianceInvestigationAssignmentDto;
import com.tss.aml.dto.result.CaseResponseDto;
import com.tss.aml.dto.result.ComplianceInvestigationAssignmentResponseDto;
import com.tss.aml.dto.result.CustomerResponseDto;
import com.tss.aml.dto.result.PaginatedResponseDto;
import com.tss.aml.enums.CaseStatus;
import com.tss.aml.enums.NotificationType;
import com.tss.aml.exception.ClosedCaseException;
import com.tss.aml.exception.ResourceNotFoundException;
import com.tss.aml.mapper.CustomerResponseDtoMapper;
import com.tss.aml.tenant.entity.BrokenRule;
import com.tss.aml.tenant.entity.Case;
import com.tss.aml.tenant.entity.ComplianceInvestigationAssignment;
import com.tss.aml.tenant.entity.Customer;
import com.tss.aml.tenant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
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
        
        // Mark all active alerts for this customer as inactive (assigned)
//        List<BrokenRule> activeRules = brokenRuleRepository.findByCustomerCustomerNumberAndActiveTrue(request.getCustomerNumber());
//        activeRules.forEach(br -> br.setActive(false));
//        brokenRuleRepository.saveAll(activeRules);

        // Notify Compliance Officer
        inAppNotificationService.createNotification(
                data.getComplianceOfficer().getEmail(),
                com.tss.aml.enums.Role.COMPLIANCE_OFFICER,
                NotificationType.CASE_ASSIGNED,
                "A new investigation for customer " + request.getCustomerNumber() + " has been assigned to you."
        );
    }

    @Transactional
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

    @Transactional
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

    public PaginatedResponseDto<ComplianceInvestigationAssignmentResponseDto> getAssignments(Pageable pageable) {
        Page<ComplianceInvestigationAssignment> assignmentPage =
                complianceInvestigationAssignmentRepository
                        .findByComplianceOfficerId
                                (
                                        complianceOfficerRepository
                                                .findByEmail(appUserService.get().getUsername())
                                                .orElseThrow(() ->
                                                        new ResourceNotFoundException("Investigation Assignment", appUserService.get().getUsername())
                                                )
                                                .getId(),
                                        pageable
                                );

        List<ComplianceInvestigationAssignmentResponseDto> dtos = assignmentPage
                .stream()
                .map(I -> {
                            ComplianceInvestigationAssignmentResponseDto dto = new ComplianceInvestigationAssignmentResponseDto();
                            dto.setCustomerResponseDto(customerResponseDtoMapper.mapCustomer(I.getCustomer()));
                            dto.setRiskScore(I.getRiskScore());
                            dto.setIsOpen(I.getIsOpen());
                            dto.setAlerts(
                                brokenRuleRepository
                                    .findByCustomerCustomerNumberAndActiveTrue(I.getCustomer().getCustomerNumber())
                                    .stream()
                                    .map(customerResponseDtoMapper::mapAlert)
                                    .toList()
                            );

                            return dto;
                        }
                )
                .toList();

        return PaginatedResponseDto.<ComplianceInvestigationAssignmentResponseDto>builder()
                .content(dtos)
                .pageNumber(assignmentPage.getNumber())
                .pageSize(assignmentPage.getSize())
                .totalElements(assignmentPage.getTotalElements())
                .totalPages(assignmentPage.getTotalPages())
                .last(assignmentPage.isLast())
                .build();
    }

    public PaginatedResponseDto<CaseResponseDto> getCases(Pageable pageable) {
        Page<Case> casePage = caseRepository.findAll(pageable);
        
        List<CaseResponseDto> dtos = casePage.getContent().stream()
                .map(c -> {
                    CaseResponseDto dto = new CaseResponseDto();
                    dto.setId(c.getId());
                    dto.setCaseName(c.getCaseName());
                    dto.setCaseDescription(c.getCaseDescription());
                    dto.setCaseStatus(c.getCaseStatus());
                    dto.setCustomerNumber(c.getInvestigatedCustomer().getCustomer().getCustomerNumber());
                    dto.setSarFiled(c.isSarFiled());
                    return dto;
                })
                .toList();

        return PaginatedResponseDto.<CaseResponseDto>builder()
                .content(dtos)
                .pageNumber(casePage.getNumber())
                .pageSize(casePage.getSize())
                .totalElements(casePage.getTotalElements())
                .totalPages(casePage.getTotalPages())
                .last(casePage.isLast())
                .build();
    }

    public boolean hasOpenCase(String customerNumber) {
        return caseRepository.existsByInvestigatedCustomerCustomerAndInvestigatedCustomerIsOpenTrue(customerNumber);
    }

    public PaginatedResponseDto<CaseResponseDto> getEscalatedCases(Pageable pageable) {
        return getCasesByStatus(CaseStatus.ESCALATED, pageable);
    }

    public PaginatedResponseDto<CaseResponseDto> getCasesByStatus(CaseStatus status, Pageable pageable) {
        Page<Case> casePage = caseRepository.findByCaseStatus(status, pageable);
        
        List<CaseResponseDto> dtos = casePage.getContent().stream()
                .map(c -> {
                    CaseResponseDto dto = new CaseResponseDto();
                    dto.setId(c.getId());
                    dto.setCaseName(c.getCaseName());
                    dto.setCaseDescription(c.getCaseDescription());
                    dto.setCaseStatus(c.getCaseStatus());
                    dto.setCustomerNumber(c.getInvestigatedCustomer().getCustomer().getCustomerNumber());
                    dto.setSarFiled(c.isSarFiled());
                    return dto;
                })
                .toList();

        return PaginatedResponseDto.<CaseResponseDto>builder()
                .content(dtos)
                .pageNumber(casePage.getNumber())
                .pageSize(casePage.getSize())
                .totalElements(casePage.getTotalElements())
                .totalPages(casePage.getTotalPages())
                .last(casePage.isLast())
                .build();
    }

    @Transactional
    public void escalateCase(UUID caseId) {
        Case caseData = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", caseId));
        
        if (caseData.getCaseStatus() == CaseStatus.CLOSED) {
            throw new ClosedCaseException();
        }
        
        caseData.setCaseStatus(CaseStatus.ESCALATED);
        caseRepository.save(caseData);

        // Notify Bank Admin
        notifyBankAdmin(NotificationType.CASE_ESCALATED, "Case " + caseData.getCaseName() + " has been escalated.");
    }

    @Transactional
    public void fileSAR(UUID caseId) {
        Case caseData = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", caseId));
        
        if (caseData.getCaseStatus() == CaseStatus.CLOSED) {
            throw new ClosedCaseException();
        }
        
        caseData.setSarFiled(true);
        caseRepository.save(caseData);

        // Notify Bank Admin
        notifyBankAdmin(NotificationType.SAR_FILED, "SAR/STR has been filed for case " + caseData.getCaseName() + ".");
    }

    @Transactional
    public void closeCase(UUID caseId) {
        Case caseData = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", caseId));
        
        if (caseData.getCaseStatus() != CaseStatus.OPEN) {
            throw new IllegalStateException("Case can only be closed if it is in OPEN status");
        }
        
        caseData.setCaseStatus(CaseStatus.CLOSED);
        caseRepository.save(caseData);
    }

    private void notifyBankAdmin(NotificationType type, String message) {
        bankAdminRepository.findAll().stream().findFirst().ifPresent(admin -> 
            inAppNotificationService.createNotification(admin.getEmail(), com.tss.aml.enums.Role.BANK_ADMIN, type, message)
        );
    }
}