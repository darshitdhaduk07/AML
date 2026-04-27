package com.tss.aml.tenant.repository;

import com.tss.aml.tenant.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByCustomerNumber(String customerNumber);

    @Query("SELECT c.customerNumber FROM Customer c WHERE c.customerNumber IN :customerNumbers")
    Set<String> findExistingCustomerNumbers(@Param("customerNumbers") Set<String> customerNumbers);
}
