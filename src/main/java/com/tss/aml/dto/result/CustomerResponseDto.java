package com.tss.aml.dto.result;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CustomerResponseDto {
    private String customerNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String familyCode;
    private LocalDate dob;
    private String occupation;
    private String nationalityCountry;
    private String countryOfBirth;
    private BigDecimal income;
    private BigDecimal netWorth;
    private List<TransactionResponseDto> transactions;
}
