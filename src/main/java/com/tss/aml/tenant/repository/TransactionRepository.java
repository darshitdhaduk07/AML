package com.tss.aml.tenant.repository;

import com.tss.aml.tenant.entity.Customer;
import com.tss.aml.tenant.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByEvaluatedFalse();

    long countByEvaluatedFalse();

    @Modifying
    @Query(value = "UPDATE transactions SET evaluated = true, updated_at = NOW() WHERE evaluated = false", nativeQuery = true)
    int markAllAsEvaluated();

    long countByCustomerAndTxnTimeAfter(Customer customer, LocalDateTime fromTime);

    List<Transaction> findByCustomerAndTxnTimeAfter(Customer lastCustomer, LocalDateTime lastFromTime);
}
