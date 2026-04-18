package com.tss.aml.entity;

import com.tss.aml.enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @NotBlank
    private String accountId;

    @NotBlank
    @Size(min = 8, max = 20)
    @Pattern(regexp = "^[0-9]+$", message = "Account number must contain only digits")
    private String accountNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @NotBlank
    private String customerId;
}
