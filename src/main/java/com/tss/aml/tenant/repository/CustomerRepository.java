package com.tss.aml.tenant.repository;

import com.tss.aml.tenant.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByCustomerNumber(String customerNumber);

}
