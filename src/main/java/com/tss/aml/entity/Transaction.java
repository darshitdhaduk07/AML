package com.tss.aml.entity;

import com.tss.aml.enums.Direction;
import com.tss.aml.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID transactionId;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String transactionNumber;

    @NotBlank
    private String accountNumber;

    @NotBlank
    private String customerNumber;

    @NotNull
    private LocalDateTime txnTime;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionType txnType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Direction direction;

    @NotBlank
    @Size(min = 2, max = 3)
    private String country;
}
