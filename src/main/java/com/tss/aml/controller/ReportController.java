package com.tss.aml.controller;

import com.tss.aml.dto.request.CaseReportData;
import com.tss.aml.reports.ReportRow;
import com.tss.aml.service.PdfReportService;
import com.tss.aml.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    public final ReportService reportService;
    public final PdfReportService pdfService;

    @GetMapping("/alert/pdf")
    public ResponseEntity<InputStreamResource> downloadReport(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {

        List<ReportRow> rows;

        if (from == null || to == null) {
            rows = reportService.getAllReport();
        } else {
            LocalDateTime fromDate = LocalDateTime.parse(from);
            LocalDateTime toDate = LocalDateTime.parse(to);

            rows = reportService.getReport(fromDate, toDate);
        }

        ByteArrayInputStream pdf = pdfService.generateReport(rows);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }
    @GetMapping("/cases/{id}/pdf")
    public ResponseEntity<InputStreamResource> downloadCaseReport(@PathVariable UUID id) {

        CaseReportData data = reportService.getCaseReport(id);

        ByteArrayInputStream pdf =
                pdfService.generateCaseReport(data.getCaseEntity(), data.getRows());

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=case-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }

}
