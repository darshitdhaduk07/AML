package com.tss.aml.dto.result;

import com.tss.aml.enums.AccountType;
import com.tss.aml.enums.Direction;
import com.tss.aml.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ParseTransaction {
    private String transactionNumber;
    private AccountType accountType;
    private LocalDateTime txnTime;
    private BigDecimal amount;
    private TransactionType txnType;
    private Direction direction;
    private String country;
    private String IFSC;
    private String customerNumber;
    private String accountNumber;
    private int rowNumber;
}
