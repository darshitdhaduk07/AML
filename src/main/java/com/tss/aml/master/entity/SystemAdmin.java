package com.tss.aml.master.entity;

import com.tss.aml.tenant.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "admins", uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
@Getter
@Setter
public class SystemAdmin extends BaseEntity {

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean isLocked = false;

    @Column(nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column
    private LocalDateTime lockTime;
}
