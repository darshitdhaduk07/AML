package com.tss.aml.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String customerId;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String customerNumber;

    @NotBlank
    private String firstName;

    private String middleName;

    @NotBlank
    private String lastName;

    private String familyCode;

    @NotNull
    private LocalDate dob;

    @NotBlank
    private String occupation;

    @NotBlank
    @Size(min = 2, max = 3)
    private String nationalityCountry;

    @NotBlank
    @Size(min = 2, max = 3)
    private String countryOfBirth;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal income;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal netWorth;
}
