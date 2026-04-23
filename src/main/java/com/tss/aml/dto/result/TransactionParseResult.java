package com.tss.aml.dto.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public class TransactionParseResult {
    private final List<ParseTransaction> transactions;
    private final List<ParseAccount> accounts;
}
