package com.tss.aml.master.entity;


import com.tss.aml.tenant.entity.BaseEntity;
import com.tss.aml.enums.TenantStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tenants", uniqueConstraints = {@UniqueConstraint(columnNames = "schema_name")})
@Getter
@Setter
public class Tenant extends BaseEntity {

    @Column(name = "tenant_name", nullable = false, length = 100)
    private String tenantName;

    @Column(name = "db_name", nullable = false, length = 100)
    private String dbName;

    @Column(name = "schema_name", nullable = false, length = 100, unique = true)
    private String schemaName;

    @Enumerated(EnumType.STRING)
    @Column(name = "tenant_status", nullable = false)
    private TenantStatus tenantStatus;


}
