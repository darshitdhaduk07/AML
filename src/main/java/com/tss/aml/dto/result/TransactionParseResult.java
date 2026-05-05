package com.tss.aml.dto.result;

import com.tss.aml.exception.ValidationException;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
@Getter
@Setter
public class TransactionParseResult {
    private final List<ParseTransaction> transactions;
    private final List<ParseAccount> accounts;
    private final List<ValidationException> errors;

    public TransactionParseResult(List<ParseTransaction> transactions, List<ParseAccount> accounts, List<ValidationException> errors) {
        this.transactions = transactions;
        this.accounts = accounts;
        this.errors = errors;
    }
}
