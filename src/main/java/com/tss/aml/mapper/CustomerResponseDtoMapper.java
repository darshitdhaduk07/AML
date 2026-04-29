package com.tss.aml.mapper;

import com.tss.aml.dto.result.CustomerResponseDto;
import com.tss.aml.dto.result.TransactionResponseDto;
import com.tss.aml.tenant.entity.Customer;
import com.tss.aml.tenant.entity.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerResponseDtoMapper {
    public CustomerResponseDto mapCustomer(Customer customer) {

        CustomerResponseDto dto = new CustomerResponseDto();

        dto.setCustomerNumber(customer.getCustomerNumber());
        dto.setFirstName(customer.getFirstName());
        dto.setMiddleName(customer.getMiddleName());
        dto.setLastName(customer.getLastName());
        dto.setFamilyCode(customer.getFamilyCode());
        dto.setDob(customer.getDob());
        dto.setOccupation(customer.getOccupation());
        dto.setNationalityCountry(customer.getNationalityCountry());
        dto.setCountryOfBirth(customer.getCountryOfBirth());
        dto.setIncome(customer.getIncome());
        dto.setNetWorth(customer.getNetWorth());

        if (customer.getTransactions() != null) {
            List<TransactionResponseDto> txns = customer.getTransactions()
                    .stream()
                    .map(this::mapTransaction)
                    .toList();

            dto.setTransactions(txns);
        }

        return dto;
    }
    public TransactionResponseDto mapTransaction(Transaction t) {

        TransactionResponseDto dto = new TransactionResponseDto();

        dto.setTransactionNumber(t.getTransactionNumber());
        dto.setAmount(t.getAmount());
        dto.setTxnType(t.getTxnType());
        dto.setDirection(t.getDirection());
        dto.setTxnTime(t.getTxnTime());
        dto.setCountry(t.getCountry());
        dto.setAccountType(t.getAccountType());
        dto.setAccount(t.getAccount().getAccountNumber());
        dto.setIFSC(t.getIFSC());

        return dto;
    }
}
