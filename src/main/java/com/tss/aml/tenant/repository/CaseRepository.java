package com.tss.aml.tenant.repository;

import com.tss.aml.tenant.entity.Case;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CaseRepository extends JpaRepository<Case, UUID> {

}
