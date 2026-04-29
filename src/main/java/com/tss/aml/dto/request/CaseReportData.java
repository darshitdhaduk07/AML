package com.tss.aml.dto.request;

import com.tss.aml.reports.CaseReportRow;
import com.tss.aml.tenant.entity.Case;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CaseReportData {
    private Case caseEntity;
    private List<CaseReportRow> rows;
}