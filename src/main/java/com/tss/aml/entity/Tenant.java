package com.tss.aml.entity;


import com.tss.aml.enums.TenateStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenants", uniqueConstraints = {@UniqueConstraint(columnNames = "schema_name")})
@Getter
@Setter
public class Tenant extends BaseEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "tenant_id", updatable = false, nullable = false)
    private UUID tenantId;

    @Column(name = "tenant_name", nullable = false, length = 100)
    private String tenantName;

    @Column(name = "db_name", nullable = false, length = 100)
    private String dbName;

    @Column(name = "schema_name", nullable = false, length = 100, unique = true)
    private String schemaName;

    @Enumerated(EnumType.STRING)
    @Column(name = "tenant_status", nullable = false)
    private TenateStatus tenantStatus;


}
