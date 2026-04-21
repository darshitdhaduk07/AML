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
import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String customerNumber;

    @Column(nullable = false)
    private String firstName;

    private String middleName;

    @Column(nullable = false)
    private String lastName;

    private String familyCode;

    @Column(nullable = false)
    private LocalDate dob;

    @Column(nullable = false)
    private String occupation;

    @Column(nullable = false, length = 3)
    private String nationalityCountry;

    @Column(nullable = false, length = 3)
    private String countryOfBirth;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal income;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal netWorth;


    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;
}
