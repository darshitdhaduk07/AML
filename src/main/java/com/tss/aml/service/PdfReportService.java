package com.tss.aml.service;

import com.tss.aml.reports.CaseReportRow;
import com.tss.aml.reports.ReportRow;
import com.tss.aml.tenant.entity.Case;
import com.tss.aml.tenant.entity.Customer;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import static com.tss.aml.constant.GlobalConstants.REPORT_DIR;

@Service
public class PdfReportService {

    public ByteArrayInputStream generateReport(List<ReportRow> rows) {

        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Flagged Transaction Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("Generated At: " + formatDate(LocalDateTime.now())));
            document.add(new Paragraph("Total Alerts: " + rows.size()));
            document.add(new Paragraph(" "));

            Map<String, List<ReportRow>> grouped =
                    rows.stream().collect(Collectors.groupingBy(ReportRow::getCustomerNumber));

            Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD);

            for (String customer : grouped.keySet()) {

                Paragraph customerHeader = new Paragraph("Customer: " + customer, sectionFont);
                customerHeader.setSpacingBefore(10f);
                customerHeader.setSpacingAfter(5f);
                document.add(customerHeader);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{2, 2, 1, 2, 3});

                addHeader(table, "Txn No");
                addHeader(table, "Rule");
                addHeader(table, "Score");
                addHeader(table, "Amount");
                addHeader(table, "Alert Time");

                for (ReportRow row : grouped.get(customer)) {
                    table.addCell(row.getTxnNumber());
                    table.addCell(row.getRuleCode());
                    table.addCell(String.valueOf(row.getScore()));
                    table.addCell(formatAmount(row.getAmount()));
                    table.addCell(formatDate(row.getAlertTime()));
                }

                document.add(table);

                document.add(new Paragraph("Total Alerts for Customer: " + grouped.get(customer).size()));
                document.add(new Paragraph(" "));
            }

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addHeader(PdfPTable table, String title) {
        Font font = new Font(Font.HELVETICA, 12, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(title, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private String formatDate(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return time.format(formatter);
    }

    private String formatAmount(BigDecimal amount) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amount);
    }
    public ByteArrayInputStream generateCaseReport(Case c, List<CaseReportRow> rows) {

        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 11);

            Paragraph title = new Paragraph("CASE REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10f);
            document.add(title);

            document.add(new Paragraph("Case Name: " + c.getCaseName(), normalFont));
            document.add(new Paragraph("Status: " + c.getCaseStatus(), normalFont));
            document.add(new Paragraph("Ground of Suspicion: " + c.getCaseDescription(), normalFont));
            document.add(new Paragraph("Details of Investigation: Based on triggered AML rules", normalFont));

            document.add(new Paragraph(" "));

            Customer customer = c.getInvestigatedCustomer().getCustomer();

            document.add(new Paragraph("Customer Number: " + customer.getCustomerNumber(), sectionFont));
            document.add(new Paragraph("Name: " + customer.getFirstName() + " " + customer.getLastName()));
            document.add(new Paragraph("Country: " + customer.getNationalityCountry()));
            document.add(new Paragraph("Occupation: " + customer.getOccupation()));
            document.add(new Paragraph("Income: " + formatAmount(customer.getIncome())));
            document.add(new Paragraph("Net Worth: " + formatAmount(customer.getNetWorth())));

            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            addHeader(table, "Txn No");
            addHeader(table, "Rule");
            addHeader(table, "Score");
            addHeader(table, "Amount");
            addHeader(table, "Time");

            for (CaseReportRow r : rows) {
                table.addCell(r.getTxnNumber());
                table.addCell(r.getRuleCode());
                table.addCell(String.valueOf(r.getScore()));
                table.addCell(formatAmount(r.getAmount()));
                table.addCell(formatDate(r.getAlertTime()));
            }

            document.add(table);

            document.add(new Paragraph(" "));

            document.add(new Paragraph("Narrative:", sectionFont));
            document.add(new Paragraph(generateNarrative(rows), normalFont));

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
    public void saveCaseReport(Case c, List<CaseReportRow> rows) {
        ByteArrayInputStream pdf = generateCaseReport(c, rows);
        try {
            Path path = Paths.get(REPORT_DIR + c.getId() + ".pdf");
            Files.createDirectories(path.getParent());
            Files.write(path, pdf.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save PDF", e);
        }
    }

    private String generateNarrative(List<CaseReportRow> rows) {

        int totalRisk = rows.stream()
                .mapToInt(CaseReportRow::getScore)
                .sum();

        return "Customer triggered " + rows.size() +
                " alerts with total risk score of " + totalRisk +
                ", indicating potentially suspicious financial behavior requiring further review.";
    }
}
