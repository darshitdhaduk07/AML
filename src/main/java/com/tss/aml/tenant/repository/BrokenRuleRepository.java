package com.tss.aml.tenant.repository;

import com.tss.aml.tenant.entity.BrokenRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BrokenRuleRepository extends JpaRepository<BrokenRule, UUID> {
}
