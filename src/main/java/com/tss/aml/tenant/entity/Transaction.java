package com.tss.aml.tenant.entity;

import com.tss.aml.enums.AccountType;
import com.tss.aml.enums.Direction;
import com.tss.aml.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String transactionNumber;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @Column(nullable = false)
    private LocalDateTime txnTime;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType txnType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Direction direction;

    @Column(nullable = false, length = 3)
    private String country;

    @Column(nullable = false)
    private String IFSC;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_number",referencedColumnName = "customerNumber", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_number",referencedColumnName = "accountNumber",nullable = false)
    private Account account;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean evaluated;
}