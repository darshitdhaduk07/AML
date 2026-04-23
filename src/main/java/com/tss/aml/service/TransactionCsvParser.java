package com.tss.aml.service;

import com.tss.aml.dto.result.ParseAccount;
import com.tss.aml.dto.result.ParseTransaction;
import com.tss.aml.dto.result.TransactionParseResult;
import com.tss.aml.enums.AccountType;
import com.tss.aml.tenant.entity.Account;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tss.aml.constant.GlobalConstants.CSV_DELIMITER;
import static com.tss.aml.constant.GlobalConstants.TRANSACTION_EXPECTED_HEADERS;

@Component
public class TransactionCsvParser {
    private final Map<String, ParseAccount> accountMap = new HashMap<>();
    private final List<ParseTransaction> transactions = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    public TransactionParseResult parse(InputStream inputStream) throws IOException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String headerLine = reader.readLine();
            if (headerLine == null) throw new IllegalArgumentException("CSV file is empty");

            validateHeaders(headerLine.trim().split(CSV_DELIMITER));

            String line;
            int lineNumber = 2;

            while ((line = reader.readLine()) != null) {

                if (line.isBlank()) {
                    lineNumber++;
                    continue;
                }

                try {
                    parseLine(line,lineNumber);
                } catch (Exception e) {
                    errors.add("Line " + lineNumber + ": " + e.getMessage());
                }

                lineNumber++;
            }
        }

        if (!errors.isEmpty()) {
            throw new CsvParseException("Errors:\n" + String.join("\n", errors));
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
                throw new IllegalArgumentException(
                        "Invalid header at column " + (i + 1) +
                                ": expected '" + TRANSACTION_EXPECTED_HEADERS[i] +
                                "' but got '" + (i < actualHeaders.length ? actualHeaders[i] : "missing") + "'"
                );
            }
        }
    }

    private void parseLine(String line, int lineNumber) {

        String[] fields = line.split(CSV_DELIMITER, -1);

        if (fields.length != TRANSACTION_EXPECTED_HEADERS.length) {
            throw new IllegalArgumentException("Invalid column count");
        }

        String transactionNumber = require(fields[0], "transaction_number");
        String accountNumber = require(fields[1], "account_number");
        String customerNumber = require(fields[2], "customer_number");

        LocalDateTime txnTime = parseDateTime(fields[3], "txn_time");
        BigDecimal amount = parseDecimal(fields[4], "amount");
        TransactionType txnType = parseEnum(fields[5], "txn_type", TransactionType.class);
        Direction direction = parseEnum(fields[6], "direction", Direction.class);
        String country = require(fields[7], "country");

        AccountType accountType = parseEnum(fields[8], "account_type", AccountType.class);
        String ifsc = require(fields[9], "IFSC");

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
        t.setTxnTime(txnTime);
        t.setAmount(amount);
        t.setTxnType(txnType);
        t.setDirection(direction);
        t.setCountry(country);
        t.setAccountNumber(accountNumber);
        t.setCustomerNumber(customerNumber);

        transactions.add(t);

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