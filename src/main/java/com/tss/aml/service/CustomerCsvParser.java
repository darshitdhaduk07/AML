package com.tss.aml.service;

import com.tss.aml.tenant.entity.Customer;
import com.tss.aml.exception.CsvParseException;
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
public class CustomerCsvParser {

    public List<Customer> parse(InputStream inputStream) throws IOException {
        List<Customer> customers = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String headerLine = reader.readLine();

            if (headerLine == null) throw new IllegalArgumentException("CSV file is empty");

            validateHeaders(headerLine.trim().split(CSV_DELIMITER));

            String line;
            int lineNumber = 2; // starts after header

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) { lineNumber++; continue; }

                try {
                    Customer customer = parseLine(line.trim(), lineNumber);
                    customers.add(customer);
                } catch (Exception e) {
                    errors.add("Line " + lineNumber + ": " + e.getMessage());
                }
                lineNumber++;
            }
        }

        if (!errors.isEmpty()) {
            throw new CsvParseException("CSV parsing failed with errors:\n" + String.join("\n", errors));
        }

        return customers;
    }

    private void validateHeaders(String[] actualHeaders) {
        for (int i = 0; i < CUSTOMER_EXPECTED_HEADERS.length; i++) {
            if (i >= actualHeaders.length ||
                    !CUSTOMER_EXPECTED_HEADERS[i].equalsIgnoreCase(actualHeaders[i].trim())) {
                throw new IllegalArgumentException(
                        "Invalid header at column " + (i + 1) +
                                ": expected '" + CUSTOMER_EXPECTED_HEADERS[i] +
                                "' but got '" + (i < actualHeaders.length ? actualHeaders[i] : "missing") + "'"
                );
            }
        }
    }

    private Customer parseLine(String line, int lineNumber) {
        String[] fields = line.split(CSV_DELIMITER, -1); // -1 keeps trailing empty strings

        if (fields.length != CUSTOMER_EXPECTED_HEADERS.length) {
            throw new IllegalArgumentException(
                    "Expected " + CUSTOMER_EXPECTED_HEADERS.length + " columns but got " + fields.length
            );
        }

        Customer customer = new Customer();
        customer.setCustomerNumber(require(fields[0],  "customer_number"));
        customer.setFirstName(require(fields[1],       "first_name"));
        customer.setMiddleName(nullable(fields[2]));   // optional
        customer.setLastName(require(fields[3],        "last_name"));
        customer.setFamilyCode(nullable(fields[4]));   // optional
        customer.setDob(parseDate(fields[5],           "dob"));
        customer.setOccupation(require(fields[6],      "occupation"));
        customer.setNationalityCountry(require(fields[7], "nationality_country"));
        customer.setCountryOfBirth(require(fields[8],  "country_of_birth"));
        customer.setIncome(parseDecimal(fields[9],     "income"));
        customer.setNetWorth(parseDecimal(fields[10],  "net_worth"));
        System.out.println(customer);
        return customer;
    }

    // --- helpers ---

    private String require(String value, String fieldName) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("'" + fieldName + "' is required");
        return value.trim();
    }

    private String nullable(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private LocalDate parseDate(String value, String fieldName) {
        try {
            return LocalDate.parse(require(value, fieldName)); // expects yyyy-MM-dd
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("'" + fieldName + "' must be yyyy-MM-dd format, got: " + value);
        }
    }

    private BigDecimal parseDecimal(String value, String fieldName) {
        try {
            return new BigDecimal(require(value, fieldName));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + fieldName + "' must be a valid number, got: " + value);
        }
    }
}
