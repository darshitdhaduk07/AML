package com.tss.aml.service;

import com.tss.aml.dto.request.CaseReportData;
import com.tss.aml.dto.result.CaseResponseDto;
import com.tss.aml.reports.CaseReportRow;
import com.tss.aml.reports.ReportRow;
import com.tss.aml.tenant.entity.Case;
import com.tss.aml.tenant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final BrokenRuleRepository brokenRuleRepository;
    private final CaseRepository caseRepository;
    private final ComplianceOfficerRepository complianceOfficerRepository;
    private final ComplianceInvestigationAssignmentRepository assignmentRepository;
    private final BatchSummaryRepository batchSummaryRepository;

    public List<ReportRow> getReport(LocalDateTime from, LocalDateTime to) {

        List<ReportRow> rows = brokenRuleRepository.getReportDataBetween(from, to);

//        Map<String, Integer> totalRiskMap = rows.stream()
//                .collect(Collectors.groupingBy(
//                        ReportRow::getTxnNumber,
//                        Collectors.summingInt(ReportRow::getScore)
//                ));

        return rows.stream()
                .map(r -> new ReportRow(
                        r.getTxnNumber(),
                        r.getCustomerNumber(),
                        r.getRuleCode(),
                        r.getScore(),
                        r.getAlertTime(),
                        r.getAmount()
                ))
                .toList();
    }

    public List<ReportRow> getAllReport() {
        return brokenRuleRepository.getReportData();
    }
    public CaseReportData getCaseReport(UUID caseId) {

        Case c = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));

        String customerNumber = c.getInvestigatedCustomer().getCustomer().getCustomerNumber();

        List<CaseReportRow> rows =
                caseRepository.getCaseDataByCustomer(customerNumber);

        return new CaseReportData(c, rows);
    }

    public List<CaseResponseDto> getSarLogs() {
        return caseRepository.findBySarFiledTrue().stream()
                .map(c -> {
                    com.tss.aml.dto.result.CaseResponseDto dto = new com.tss.aml.dto.result.CaseResponseDto();
                    dto.setId(c.getId());
                    dto.setCaseName(c.getCaseName());
                    dto.setCaseDescription(c.getCaseDescription());
                    dto.setCaseStatus(c.getCaseStatus());
                    dto.setCustomerNumber(c.getInvestigatedCustomer().getCustomer().getCustomerNumber());
                    dto.setSarFiled(c.isSarFiled());
                    return dto;
                })
                .toList();
    }

    public List<Map<String, Object>> getCoPerformance() {
        return complianceOfficerRepository.findAll().stream()
                .map(co -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", co.getEmail());
                    map.put("email", co.getEmail());
                    map.put("total", assignmentRepository.countByComplianceOfficerId(co.getId()));
                    map.put("active", assignmentRepository.countByComplianceOfficerIdAndIsOpenTrue(co.getId()));
                    map.put("completed", assignmentRepository.countByComplianceOfficerIdAndIsOpenFalse(co.getId()));
                    return map;
                })
                .toList();
    }

    @Transactional
    public List<com.tss.aml.tenant.entity.BatchSummary> getBatchSummaries() {
        return batchSummaryRepository.findAllByOrderByCreatedAtDesc();
    }

}
