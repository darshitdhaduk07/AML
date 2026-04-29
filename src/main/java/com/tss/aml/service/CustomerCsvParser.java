package com.tss.aml.service;

import com.tss.aml.exception.BulkValidationException;
import com.tss.aml.exception.ValidationException;
import com.tss.aml.tenant.entity.Customer;
import com.tss.aml.exception.CsvParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static com.tss.aml.constant.GlobalConstants.CSV_DELIMITER;
import static com.tss.aml.constant.GlobalConstants.CUSTOMER_EXPECTED_HEADERS;

@Component
@Slf4j
public class CustomerCsvParser {

    public List<Customer> parse(InputStream inputStream) throws IOException {
        List<Customer> customers = new ArrayList<>();
        List<ValidationException> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String headerLine = reader.readLine();

            if (headerLine == null) throw new CsvParseException("CSV file is empty");

            validateHeaders(headerLine.trim().split(CSV_DELIMITER));

            String line;
            int lineNumber = 2; // starts after header

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    lineNumber++;
                    continue;
                }

                try {
                    Customer customer = parseLine(line.trim(), lineNumber);
                    customers.add(customer);
                } catch (BulkValidationException e) {
                    log.warn("Validation error on line {}: {}", lineNumber, e.getMessage());
                    errors.addAll(e.getErrors());
                }
                lineNumber++;
            }

            if (!errors.isEmpty()) {
                throw new BulkValidationException(errors);
            }
        }

        return customers;
    }

    private void validateHeaders(String[] actualHeaders) {
        for (int i = 0; i < CUSTOMER_EXPECTED_HEADERS.length; i++) {
            if (i >= actualHeaders.length ||
                    !CUSTOMER_EXPECTED_HEADERS[i].equalsIgnoreCase(actualHeaders[i].trim())) {
                throw new CsvParseException(
                        "Invalid header at column " + (i + 1) +
                                ": expected '" + CUSTOMER_EXPECTED_HEADERS[i] +
                                "' but got '" + (i < actualHeaders.length ? actualHeaders[i] : "missing") + "'"
                );
            }
        }
    }

    private Customer parseLine(String line, int lineNumber) {
        String[] fields = line.split(CSV_DELIMITER, -1); // -1 keeps trailing empty strings

        List<ValidationException> rowErrors = new ArrayList<>();

        if (fields.length != CUSTOMER_EXPECTED_HEADERS.length) {
            throw new ValidationException(
                    "row",
                    line,
                    "INVALID_COLUMN_COUNT",
                    "Expected " + CUSTOMER_EXPECTED_HEADERS.length +
                            " columns but got " + fields.length,
                    lineNumber
            );
        }

        Customer customer = new Customer();
        customer.setCustomerNumber(require(fields[0], "customer_number", lineNumber,rowErrors,null));
        customer.setFirstName(require(fields[1], "first_name", lineNumber,rowErrors,null));
        customer.setMiddleName(nullable(fields[2]));   // optional
        customer.setLastName(require(fields[3], "last_name", lineNumber,rowErrors,null));
        customer.setFamilyCode(nullable(fields[4]));   // optional
        customer.setDob(parseDate(fields[5], "dob", lineNumber,rowErrors));
        customer.setOccupation(require(fields[6], "occupation", lineNumber,rowErrors,null));
        customer.setNationalityCountry(require(fields[7], "nationality_country", lineNumber,rowErrors,3));
        customer.setCountryOfBirth(require(fields[8], "country_of_birth", lineNumber,rowErrors,3));
        customer.setIncome(parseDecimal(fields[9], "income", lineNumber,rowErrors));
        customer.setNetWorth(parseDecimal(fields[10], "net_worth", lineNumber,rowErrors));
        log.debug("Parsed customer: {}", customer.getCustomerNumber());

        if (!rowErrors.isEmpty()) {
            throw new BulkValidationException(rowErrors);
        }
        return customer;


    }

    // --- helpers ---

    private String require(String value, String fieldName, int lineNumber,List<ValidationException> rowErrors,        Integer maxLength   // 👈 ADD THIS
    ) {
        if (value == null || value.isBlank()) {
             rowErrors.add(new ValidationException(
                    fieldName,
                    value,
                    "MISSING_FIELD",
                    fieldName + " is required",
                    lineNumber
            ));
             return null;
        }
        String trimmed = value.trim();

        if (maxLength != null && trimmed.length() > maxLength) {
            rowErrors.add(new ValidationException(
                    fieldName,
                    value,
                    "MAX_LENGTH_EXCEEDED",
                    fieldName + " must be <= " + maxLength + " characters",
                    lineNumber
            ));
            return null;
        }

        return trimmed;
    }

    private String nullable(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private LocalDate parseDate(String value, String fieldName, int lineNumber, List<ValidationException> rowErrors) {
        String v = require(value, fieldName, lineNumber, rowErrors,null);
        if (v == null) return null;

        try {
            return LocalDate.parse(v);
        } catch (DateTimeParseException e) {
            rowErrors.add(new ValidationException(
                    fieldName,
                    value,
                    "INVALID_DATE_FORMAT",
                    fieldName + " must be yyyy-MM-dd format",
                    lineNumber
            ));
            return null;
        }
    }

    private BigDecimal parseDecimal(String value, String fieldName, int lineNumber, List<ValidationException> rowErrors) {
        String v = require(value, fieldName, lineNumber, rowErrors,null);
        if (v == null) return null;

        try {
            return new BigDecimal(v);
        } catch (NumberFormatException e) {
            rowErrors.add(new ValidationException(
                    fieldName,
                    value,
                    "INVALID_NUMBER",
                    fieldName + " must be a valid number",
                    lineNumber
            ));
            return null;
        }
    }
}
