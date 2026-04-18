package com.tss.aml.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "compliance_officers")
public class ComplianceOfficer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID complianceOfficerId;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$", message = "Password must contain uppercase, lowercase, number, and special character")
    private String password;

    @Column(nullable = false)
    private boolean isSuspended = false;
}
