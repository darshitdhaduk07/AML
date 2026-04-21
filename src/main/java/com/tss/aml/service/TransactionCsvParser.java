package com.tss.aml.service;

import com.tss.aml.tenant.entity.Transaction;
import com.tss.aml.enums.Direction;
import com.tss.aml.enums.TransactionType;
import com.tss.aml.exception.CsvParseException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static com.tss.aml.constant.GlobalConstants.CSV_DELIMITER;
import static com.tss.aml.constant.GlobalConstants.TRANSACTION_EXPECTED_HEADERS;

@Component
public class TransactionCsvParser {

    public List<Transaction> parse(InputStream inputStream) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
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
                    Transaction transaction = parseLine(line.trim(), lineNumber);
                    transactions.add(transaction);
                } catch (Exception e) {
                    errors.add("Line " + lineNumber + ": " + e.getMessage());
                }
                lineNumber++;
            }
        }

        if (!errors.isEmpty()) {
            throw new CsvParseException("CSV parsing failed with errors:\n" + String.join("\n", errors));
        }

        return transactions;
    }

    private void validateHeaders(String[] actualHeaders) {
        for (int i = 0; i < TRANSACTION_EXPECTED_HEADERS.length; i++) {
            if (i >= actualHeaders.length ||
                    !TRANSACTION_EXPECTED_HEADERS[i].equalsIgnoreCase(actualHeaders[i].trim())) {
                throw new IllegalArgumentException(
                        "Invalid header at column " + (i + 1) +
                                ": expected '" + TRANSACTION_EXPECTED_HEADERS[i] +
                                "' but got '" + (i < actualHeaders.length ? actualHeaders[i] : "missing") + "'"
                );
            }
        }
    }

    private Transaction parseLine(String line, int lineNumber) {
        String[] fields = line.split(CSV_DELIMITER, -1); // -1 keeps trailing empty strings

        if (fields.length != TRANSACTION_EXPECTED_HEADERS.length) {
            throw new IllegalArgumentException(
                    "Expected " + TRANSACTION_EXPECTED_HEADERS.length + " columns but got " + fields.length
            );
        }

        Transaction transaction = new Transaction();
        transaction.setTransactionNumber(require(fields[0], "transaction_number"));
//        transaction.setA(require(fields[1],     "account_number"));
//        transaction.setCustomerNumber(require(fields[2],    "customer_number"));
        transaction.setTxnTime(parseDateTime(fields[3],     "txn_time"));
        transaction.setAmount(parseDecimal(fields[4],       "amount"));
        transaction.setTxnType(parseEnum(fields[5],         "txn_type", TransactionType.class));
        transaction.setDirection(parseEnum(fields[6],       "direction", Direction.class));
        transaction.setCountry(require(fields[7],           "country"));
        System.out.println(transaction);
        return transaction;
    }

    // --- helpers ---

    private String require(String value, String fieldName) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("'" + fieldName + "' is required");
        return value.trim();
    }

    private LocalDateTime parseDateTime(String value, String fieldName) {
        try {
            return LocalDateTime.parse(require(value, fieldName)); // expects yyyy-MM-ddTHH:mm:ss
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("'" + fieldName + "' must be ISO datetime format (yyyy-MM-ddTHH:mm:ss), got: " + value);
        }
    }

    private BigDecimal parseDecimal(String value, String fieldName) {
        try {
            BigDecimal parsed = new BigDecimal(require(value, fieldName));
            if (parsed.compareTo(BigDecimal.ZERO) <= 0)
                throw new IllegalArgumentException("'" + fieldName + "' must be greater than 0, got: " + value);
            return parsed;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + fieldName + "' must be a valid number, got: " + value);
        }
    }

    private <E extends Enum<E>> E parseEnum(String value, String fieldName, Class<E> enumClass) {
        try {
            return Enum.valueOf(enumClass, require(value, fieldName).toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("'" + fieldName + "' has invalid value: " + value);
        }
    }
}