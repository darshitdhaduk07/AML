package com.tss.aml.dto.result;

import com.tss.aml.enums.AccountType;
import com.tss.aml.tenant.entity.Customer;
import com.tss.aml.tenant.entity.Transaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ParseAccount {
    private String accountNumber;
    private AccountType accountType;
    private String IFSC;
    private String customerNumber;
}
