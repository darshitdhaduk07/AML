package com.tss.aml.tenant.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "compliance_officers")
@Getter
@Setter
public class ComplianceOfficer extends BaseEntity{

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    @Column(nullable = false)
    private boolean isSuspended = false;

    @Column(nullable = false)
    private boolean isLocked = false;

    @Column
    private LocalDateTime lockTime;
}
