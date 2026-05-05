package com.tss.aml.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tss.aml.dto.result.ParseAccount;
import com.tss.aml.dto.result.ParseTransaction;
import com.tss.aml.dto.result.TransactionParseResult;
import com.tss.aml.exception.BulkValidationException;
import com.tss.aml.exception.ValidationException;
import com.tss.aml.tenant.entity.BatchSummary;
import com.tss.aml.tenant.entity.Customer;
import com.tss.aml.tenant.repository.CustomerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataIngestionService {

    private final EntityManager entityManager;
    private final CustomerCsvParser csvParser;
    private final TransactionCsvParser transactionParser;
    private final CustomerRepository customerRepository;
    private final com.tss.aml.tenant.repository.BatchSummaryRepository batchSummaryRepository;
    private final ObjectMapper objectMapper;
    private DataIngestionService self;

    @org.springframework.beans.factory.annotation.Autowired
    public void setSelf(@org.springframework.context.annotation.Lazy DataIngestionService self) {
        this.self = self;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BatchSummary initBatchSummary(String fileName, String fileType) {
        BatchSummary summary = new BatchSummary();
        summary.setFileName(fileName);
        summary.setFileType(fileType);
        summary.setStatus("PROCESSING");
        summary.setRecordCount(0L);
        return batchSummaryRepository.save(summary);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateBatchSummary(BatchSummary summary) {
        log.info("Updating batch summary status to: {} for file: {}", summary.getStatus(), summary.getFileName());
        
        // Truncate error message if it's too long for the DB column (1000 chars)
        if (summary.getErrorMessage() != null && summary.getErrorMessage().length() > 1000) {
            summary.setErrorMessage(summary.getErrorMessage().substring(0, 997) + "...");
        }
        
        batchSummaryRepository.save(summary);
    }

    @Transactional
    public void processCustomerIngestion(BatchSummary summary, MultipartFile file) throws IOException {
        List<Customer> customers = csvParser.parse(file.getInputStream());
        log.info("Parsed {} customers from file", customers.size());
        summary.setRecordCount(customers.size());
        bulkCustomerUpsert(customers);
    }

    public void ingestCustomersFromFile(MultipartFile file) throws IOException {
        log.info("Starting customer ingestion from file: {}", file.getOriginalFilename());
        BatchSummary summary = self.initBatchSummary(file.getOriginalFilename(), "CUSTOMER");

        try {
            self.processCustomerIngestion(summary, file);
            summary.setStatus("SUCCESS");
            log.info("Completed customer ingestion");
        } catch (BulkValidationException e) {
            handleFailure(summary, e.getErrors(), "Validation failed");
            throw e;
        } catch (Exception e) {
            handleFailure(summary, null, e.getMessage());
            throw e;
        } finally {
            self.updateBatchSummary(summary);
        }
    }

    @Transactional
    public void processTransactionIngestion(BatchSummary summary, MultipartFile file) throws IOException {
        TransactionParseResult result = transactionParser.parse(file.getInputStream());
        log.info("Parsed {} transactions and {} accounts", result.getTransactions().size(), result.getAccounts().size());
        summary.setRecordCount((long) result.getTransactions().size());

        List<ValidationException> allErrors = new ArrayList<>(result.getErrors());

        // Map customer number to its FIRST appearing row number for error reporting
        Map<String, Integer> customerRowMap = new HashMap<>();
        for (ParseTransaction t : result.getTransactions()) {
            customerRowMap.putIfAbsent(t.getCustomerNumber(), t.getRowNumber());
        }
        for (ParseAccount a : result.getAccounts()) {
            customerRowMap.putIfAbsent(a.getCustomerNumber(), a.getRowNumber());
        }
        
        // Add customer existence errors to the list
        allErrors.addAll(validateCustomersExist(customerRowMap));
        
        if (!allErrors.isEmpty()) {
            throw new BulkValidationException(allErrors);
        }
        
        bulkAccountUpsert(result.getAccounts());
        bulkTransactionUpsert(result.getTransactions());
    }

    public void ingestTransactionsFromFile(MultipartFile file) throws IOException {
        log.info("Starting transaction ingestion from file: {}", file.getOriginalFilename());
        BatchSummary summary = self.initBatchSummary(file.getOriginalFilename(), "TRANSACTION");

        try {
            self.processTransactionIngestion(summary, file);
            summary.setStatus("SUCCESS");
            log.info("Completed transaction and account ingestion");
        } catch (BulkValidationException e) {
            handleFailure(summary, e.getErrors(), "Validation failed");
            throw e;
        } catch (Exception e) {
            handleFailure(summary, null, e.getMessage());
            throw e;
        } finally {
            self.updateBatchSummary(summary);
        }
    }

    private void handleFailure(BatchSummary summary, List<ValidationException> errors, String message) {
        summary.setStatus("FAILED");
        summary.setErrorMessage(message);
        summary.setRecordCount(0L);
        
        if (errors != null && !errors.isEmpty()) {
            try {
                List<Map<String, Object>> errorList = errors.stream()
                        .limit(100)
                        .map(e -> {
                            Map<String, Object> errorMap = new HashMap<>();
                            errorMap.put("field", e.getField());
                            errorMap.put("value", e.getValue());
                            errorMap.put("errorCode", e.getErrorCode());
                            errorMap.put("row", e.getRow());
                            errorMap.put("message", e.getMessage());
                            return errorMap;
                        }).collect(Collectors.toList());
                summary.setDetails(objectMapper.writeValueAsString(errorList));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize errors", e);
                summary.setDetails("Error serializing details: " + e.getMessage());
            }
        }
    }

    private void bulkCustomerUpsert(List<Customer> customers) {
        int batchSize = 500;

        for (int i = 0; i < customers.size(); i += batchSize) {
            List<Customer> batch = customers.subList(i, Math.min(i + batchSize, customers.size()));
            upsertCustomerBatch(batch);
            entityManager.flush();
            entityManager.clear(); // free memory after each batch
        }
    }

    private void upsertCustomerBatch(List<Customer> batch) {
        StringBuilder sql = new StringBuilder("""
                INSERT INTO customers (
                    id, customer_number, first_name, middle_name,
                    last_name, family_code, dob, occupation,
                    nationality_country, country_of_birth, income, net_worth,created_at, updated_at
                ) VALUES
                """);

        List<Object> params = new ArrayList<>();
        for (int i = 0; i < batch.size(); i++) {
            Customer c = batch.get(i);
            sql.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,NOW(), NOW())");
            if (i < batch.size() - 1) sql.append(", ");

            params.add(UUID.randomUUID());
            params.add(c.getCustomerNumber());
            params.add(c.getFirstName());
            params.add(c.getMiddleName());
            params.add(c.getLastName());
            params.add(c.getFamilyCode());
            params.add(Date.valueOf(c.getDob()));
            params.add(c.getOccupation());
            params.add(c.getNationalityCountry());
            params.add(c.getCountryOfBirth());
            params.add(c.getIncome());
            params.add(c.getNetWorth());
        }

        sql.append("""
                 ON CONFLICT (customer_number)
                 DO UPDATE SET
                     first_name          = EXCLUDED.first_name,
                     middle_name         = EXCLUDED.middle_name,
                     last_name           = EXCLUDED.last_name,
                     family_code         = EXCLUDED.family_code,
                     dob                 = EXCLUDED.dob,
                     occupation          = EXCLUDED.occupation,
                     nationality_country = EXCLUDED.nationality_country,
                     country_of_birth    = EXCLUDED.country_of_birth,
                     income              = EXCLUDED.income,
                     net_worth           = EXCLUDED.net_worth,
                     updated_at       = NOW()
                """);

        Query query = entityManager.createNativeQuery(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }
        query.executeUpdate();
    }

    private void bulkTransactionUpsert(List<ParseTransaction> transactions) {

        int batchSize = 500;

        for (int i = 0; i < transactions.size(); i += batchSize) {
            List<ParseTransaction> batch =
                    transactions.subList(i, Math.min(i + batchSize, transactions.size()));

            upsertTransactionBatch(batch);
            entityManager.flush();
            entityManager.clear();
        }
    }

    private void upsertTransactionBatch(List<ParseTransaction> batch) {
        StringBuilder sql = new StringBuilder("""
            INSERT INTO transactions (
                id, transaction_number, account_number, customer_number,
                txn_time, amount, txn_type, direction, country,account_type, ifsc,created_at, updated_at
            ) VALUES
            """);

        List<Object> params = new ArrayList<>();

        for (int i = 0; i < batch.size(); i++) {
            ParseTransaction t = batch.get(i);
            sql.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,NOW(), NOW())");
            if (i < batch.size() - 1) sql.append(", ");

            params.add(UUID.randomUUID());
            params.add(t.getTransactionNumber());
            params.add(t.getAccountNumber());
            params.add(t.getCustomerNumber());
            params.add(Timestamp.valueOf(t.getTxnTime()));
            params.add(t.getAmount());
            params.add(t.getTxnType().name());
            params.add(t.getDirection().name());
            params.add(t.getCountry());
            params.add(t.getAccountType().name());
            params.add(t.getIFSC());
        }

        sql.append("""
             ON CONFLICT (transaction_number)
             DO UPDATE SET
                 account_number   = EXCLUDED.account_number,
                 customer_number  = EXCLUDED.customer_number,
                 txn_time         = EXCLUDED.txn_time,
                 amount           = EXCLUDED.amount,
                 txn_type         = EXCLUDED.txn_type,
                 direction        = EXCLUDED.direction,
                 country          = EXCLUDED.country,
                 account_type     = EXCLUDED.account_type,
                 ifsc             = EXCLUDED.ifsc,
                 updated_at       = NOW()
            """);

        Query query = entityManager.createNativeQuery(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }
        query.executeUpdate();
    }

    private void upsertAccountBatch(List<ParseAccount> batch) {
        StringBuilder sql = new StringBuilder("""
        INSERT INTO accounts (
            id, account_number, account_type, ifsc, customer_number,created_at, updated_at
        ) VALUES
        """);

        List<Object> params = new ArrayList<>();

        for (int i = 0; i < batch.size(); i++) {

            ParseAccount a = batch.get(i);

            sql.append("(?, ?, ?, ?, ?,NOW(), NOW())");
            if (i < batch.size() - 1) sql.append(", ");

            params.add(UUID.randomUUID());
            params.add(a.getAccountNumber());
            params.add(a.getAccountType().name());
            params.add(a.getIFSC());
            params.add(a.getCustomerNumber());
        }

        sql.append("""
        ON CONFLICT (account_number)
        DO UPDATE SET
            account_type     = EXCLUDED.account_type,
            ifsc             = EXCLUDED.ifsc,
            customer_number  = EXCLUDED.customer_number,
            updated_at       = NOW()
       """);

        Query query = entityManager.createNativeQuery(sql.toString());

        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }

        query.executeUpdate();
    }
    private void bulkAccountUpsert(List<ParseAccount> accounts) {

        int batchSize = 500;

        for (int i = 0; i < accounts.size(); i += batchSize) {
            List<ParseAccount> batch = accounts.subList(i, Math.min(i + batchSize, accounts.size()));
            upsertAccountBatch(batch);
            entityManager.flush();
            entityManager.clear();
        }
    }

    private List<ValidationException> validateCustomersExist(Map<String, Integer> customerRowMap) {
        List<ValidationException> errors = new ArrayList<>();
        List<String> allCustomerNumbers = new ArrayList<>(customerRowMap.keySet());
        int batchSize = 500;

        for (int i = 0; i < allCustomerNumbers.size(); i += batchSize) {
            List<String> batch = allCustomerNumbers.subList(i, Math.min(i + batchSize, allCustomerNumbers.size()));
            Set<String> batchSet = new HashSet<>(batch);

            Set<String> existingDbCustomers = customerRepository.findExistingCustomerNumbers(batchSet);

            for (String csvCustomer : batch) {
                if (!existingDbCustomers.contains(csvCustomer)) {
                    errors.add(new ValidationException(
                            "customer_number", 
                            csvCustomer, 
                            "INVALID_REFERENCE", 
                            "Customer '" + csvCustomer + "' does not exist", 
                            customerRowMap.get(csvCustomer)
                    ));
                }
            }
        }

        return errors;
    }
}
