package com.tss.aml.dto.result;

import com.tss.aml.enums.AccountType;
import com.tss.aml.enums.Direction;
import com.tss.aml.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponseDto {
    private String transactionNumber;
    private AccountType accountType;
    private LocalDateTime txnTime;
    private BigDecimal amount;
    private TransactionType txnType;
    private Direction direction;
    private String country;
    private String IFSC;
    private String account;
}
