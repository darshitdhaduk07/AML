package com.tss.aml.master.repository;

import com.tss.aml.master.entity.SystemAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SystemAdminRepository extends JpaRepository<SystemAdmin, UUID> {
    Optional<SystemAdmin> findByEmail(String email);
}
