package com.tss.aml.tenant.repository;

import com.tss.aml.tenant.entity.SelectedRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SelectedRuleRepository extends JpaRepository<SelectedRule, UUID> {

}
