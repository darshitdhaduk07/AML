package com.tss.aml.service;

import com.tss.aml.dto.request.CaseReportData;
import com.tss.aml.reports.CaseReportRow;
import com.tss.aml.reports.ReportRow;
import com.tss.aml.tenant.entity.Case;
import com.tss.aml.tenant.repository.BrokenRuleRepository;
import com.tss.aml.tenant.repository.CaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final BrokenRuleRepository brokenRuleRepository;
    private final CaseRepository caseRepository;

    public List<ReportRow> getReport(LocalDateTime from, LocalDateTime to) {

        List<ReportRow> rows = brokenRuleRepository.getReportDataBetween(from, to);

        Map<String, Integer> totalRiskMap = rows.stream()
                .collect(Collectors.groupingBy(
                        ReportRow::getTxnNumber,
                        Collectors.summingInt(ReportRow::getScore)
                ));

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

}
