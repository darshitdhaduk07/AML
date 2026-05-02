package com.tss.aml.dto.result;

import com.tss.aml.enums.AccountType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParseAccount {
    private String accountNumber;
    private AccountType accountType;
    private String IFSC;
    private String customerNumber;
}
