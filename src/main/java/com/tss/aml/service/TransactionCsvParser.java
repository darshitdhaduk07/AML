package com.tss.aml.service;

import com.tss.aml.dto.result.ParseAccount;
import com.tss.aml.dto.result.ParseTransaction;
import com.tss.aml.dto.result.TransactionParseResult;
import com.tss.aml.enums.AccountType;
import com.tss.aml.exception.BulkValidationException;
import com.tss.aml.exception.ValidationException;
import com.tss.aml.tenant.entity.Account;
import com.tss.aml.tenant.entity.Transaction;
import com.tss.aml.enums.Direction;
import com.tss.aml.enums.TransactionType;
import com.tss.aml.exception.CsvParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tss.aml.constant.GlobalConstants.CSV_DELIMITER;
import static com.tss.aml.constant.GlobalConstants.TRANSACTION_EXPECTED_HEADERS;

@Slf4j
@Component
public class TransactionCsvParser {
    private final Map<String, ParseAccount> accountMap = new HashMap<>();
    private final List<ValidationException> errors = new ArrayList<>();

    public TransactionParseResult parse(InputStream inputStream) throws IOException {
        List<ValidationException> errors = new ArrayList<>();
        List<ParseTransaction> transactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String headerLine = reader.readLine();
            if (headerLine == null) throw new CsvParseException("CSV file is empty");

            validateHeaders(headerLine.trim().split(CSV_DELIMITER));

            String line;
            int lineNumber = 2;

            while ((line = reader.readLine()) != null) {

                if (line.isBlank()) {
                    lineNumber++;
                    continue;
                }

                try {
                    parseLine(line, lineNumber,transactions);
                } catch (BulkValidationException e) {
                    errors.addAll(e.getErrors());
                    System.out.println(e);
                }

                lineNumber++;
            }
        }

        if (!errors.isEmpty()) {
            throw new BulkValidationException(errors);
        }

        return new TransactionParseResult(
                transactions,
                new ArrayList<>(accountMap.values())
        );
    }

    private void validateHeaders(String[] actualHeaders) {
        for (int i = 0; i < TRANSACTION_EXPECTED_HEADERS.length; i++) {
            if (i >= actualHeaders.length ||
                    !TRANSACTION_EXPECTED_HEADERS[i].equalsIgnoreCase(actualHeaders[i].trim())) {
                throw new CsvParseException(
                        "Invalid header at column " + (i + 1) +
                                ": expected '" + TRANSACTION_EXPECTED_HEADERS[i] +
                                "' but got '" + (i < actualHeaders.length ? actualHeaders[i] : "missing") + "'"
                );
            }
        }
    }

    private void parseLine(String line, int lineNumber,List<ParseTransaction> transactions) {

        String[] fields = line.split(CSV_DELIMITER, -1);
        List<ValidationException> rowErrors = new ArrayList<>();

        if (fields.length != TRANSACTION_EXPECTED_HEADERS.length) {
            throw new ValidationException(
                    "row",
                    line,
                    "INVALID_COLUMN_COUNT",
                    "Expected " + TRANSACTION_EXPECTED_HEADERS.length +
                            " columns but got " + fields.length,
                    lineNumber
            );
        }

        String transactionNumber = require(fields[0], "transaction_number", lineNumber, rowErrors, null);
        String accountNumber     = require(fields[1], "account_number", lineNumber, rowErrors, null);
        String customerNumber    = require(fields[2], "customer_number", lineNumber, rowErrors, null);

        LocalDateTime txnTime = parseDateTime(fields[3], "txn_time", lineNumber, rowErrors);
        BigDecimal amount     = parseDecimal(fields[4], "amount", lineNumber, rowErrors);

        TransactionType txnType = parseEnum(fields[5], "txn_type", TransactionType.class, lineNumber, rowErrors);
        Direction direction     = parseEnum(fields[6], "direction", Direction.class, lineNumber, rowErrors);

        String country = require(fields[7], "country", lineNumber, rowErrors, 3);
        AccountType accountType = parseEnum(fields[8], "account_type", AccountType.class, lineNumber, rowErrors);

        String ifsc = require(fields[9], "IFSC", lineNumber, rowErrors, 11);

        if (!rowErrors.isEmpty()) {
            throw new BulkValidationException(rowErrors);
        }

        // ✔ Safe to create objects now
        ParseAccount existing = accountMap.get(accountNumber);

        if (existing == null) {
            ParseAccount acc = new ParseAccount();
            acc.setAccountNumber(accountNumber);
            acc.setAccountType(accountType);
            acc.setIFSC(ifsc);
            acc.setCustomerNumber(customerNumber);
            accountMap.put(accountNumber, acc);
        }

        ParseTransaction t = new ParseTransaction();
        t.setTransactionNumber(transactionNumber);
        t.setAccountType(accountType);
        t.setTxnTime(txnTime);
        t.setAmount(amount);
        t.setTxnType(txnType);
        t.setDirection(direction);
        t.setCountry(country);
        t.setIFSC(ifsc);
        t.setAccountNumber(accountNumber);
        t.setCustomerNumber(customerNumber);

        transactions.add(t);
    }

    // --- helpers ---

    private String require(
            String value,
            String fieldName,
            int lineNumber,
            List<ValidationException> rowErrors,
            Integer maxLength
    ) {
        if (value == null || value.isBlank()) {
            rowErrors.add(new ValidationException(
                    fieldName, value, "MISSING_FIELD",
                    fieldName + " is required", lineNumber
            ));
            return null;
        }

        String trimmed = value.trim();

        if (maxLength != null && trimmed.length() > maxLength) {
            rowErrors.add(new ValidationException(
                    fieldName, value, "MAX_LENGTH_EXCEEDED",
                    fieldName + " must be <= " + maxLength, lineNumber
            ));
            return null;
        }

        return trimmed;
    }

    private LocalDateTime parseDateTime(String value, String fieldName, int lineNumber, List<ValidationException> rowErrors) {
        String v = require(value, fieldName, lineNumber, rowErrors, null);
        if (v == null) return null;

        try {
            return LocalDateTime.parse(v);
        } catch (DateTimeParseException e) {
            rowErrors.add(new ValidationException(
                    fieldName, value, "INVALID_DATE_FORMAT",
                    fieldName + " must be yyyy-MM-ddTHH:mm:ss format",
                    lineNumber
            ));
            return null;
        }
    }

    private BigDecimal parseDecimal(String value, String fieldName, int lineNumber, List<ValidationException> rowErrors) {
        String v = require(value, fieldName, lineNumber, rowErrors, null);
        if (v == null) return null;

        try {
            BigDecimal parsed = new BigDecimal(v);

            if (parsed.compareTo(BigDecimal.ZERO) <= 0) {
                rowErrors.add(new ValidationException(
                        fieldName, value, "INVALID_AMOUNT",
                        fieldName + " must be > 0", lineNumber
                ));
                return null;
            }
            if (parsed.scale() > 4) {
                rowErrors.add(new ValidationException(
                        fieldName,
                        value,
                        "INVALID_SCALE",
                        fieldName + " max 4 decimal places",
                        lineNumber
                ));
                return null;
            }

            return parsed;

        } catch (NumberFormatException e) {
            rowErrors.add(new ValidationException(
                    fieldName, value, "INVALID_NUMBER",
                    fieldName + " must be a valid number", lineNumber
            ));
            return null;
        }
    }

    private <E extends Enum<E>> E parseEnum(String value, String fieldName, Class<E> enumClass,
                                            int lineNumber, List<ValidationException> rowErrors) {

        String v = require(value, fieldName, lineNumber, rowErrors, null);
        if (v == null) return null;

        try {
            return Enum.valueOf(enumClass, v.toUpperCase());
        } catch (IllegalArgumentException e) {
            rowErrors.add(new ValidationException(
                    fieldName, value, "INVALID_ENUM",
                    fieldName + " has invalid value: " + value,
                    lineNumber
            ));
            return null;
        }
    }
}