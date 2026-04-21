package com.tss.aml.tenant.entity;

import com.tss.aml.enums.AccountType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_number", nullable = false)
    private Customer customer;


    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;
}
