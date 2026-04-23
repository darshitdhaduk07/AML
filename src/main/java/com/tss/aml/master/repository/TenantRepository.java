package com.tss.aml.master.repository;

import com.tss.aml.master.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Object> findBySchemaName(String schemaName);
}
