package com.tss.aml.tenant.repository;

import com.tss.aml.tenant.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
}
