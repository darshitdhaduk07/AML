package com.tss.aml.service;

import com.tss.aml.entity.Customer;
import com.tss.aml.repository.CustomerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class DataIngestionService {

    private final CustomerRepository customerRepo;
    private final EntityManager entityManager;
    private final CustomerCsvParser csvParser;

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
                    customer_id, customer_number, first_name, middle_name,
                    last_name, family_code, dob, occupation,
                    nationality_country, country_of_birth, income, net_worth
                ) VALUES
                """);

        List<Object> params = new ArrayList<>();

        for (int i = 0; i < batch.size(); i++) {
            Customer c = batch.get(i);
            sql.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            if (i < batch.size() - 1) sql.append(", ");

            params.add(c.getCustomerId() != null ? c.getCustomerId() : UUID.randomUUID().toString());
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
                     customer_id         = EXCLUDED.customer_id,
                     first_name          = EXCLUDED.first_name,
                     middle_name         = EXCLUDED.middle_name,
                     last_name           = EXCLUDED.last_name,
                     family_code         = EXCLUDED.family_code,
                     dob                 = EXCLUDED.dob,
                     occupation          = EXCLUDED.occupation,
                     nationality_country = EXCLUDED.nationality_country,
                     country_of_birth    = EXCLUDED.country_of_birth,
                     income              = EXCLUDED.income,
                     net_worth           = EXCLUDED.net_worth
                """);

        Query query = entityManager.createNativeQuery(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }
        query.executeUpdate();

    }

    @Async
    @Transactional
    public void ingestCustomersFromFile(MultipartFile file) throws IOException {
        bulkCustomerUpsert(csvParser.parse(file.getInputStream()));
    }

}
