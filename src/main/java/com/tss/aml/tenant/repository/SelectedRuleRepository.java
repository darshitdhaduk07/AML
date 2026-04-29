package com.tss.aml.tenant.repository;

import com.tss.aml.tenant.entity.SelectedRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface SelectedRuleRepository extends JpaRepository<SelectedRule, UUID> {
    @Query("SELECT COALESCE(SUM(r.weight), 0) FROM SelectedRule r")
    Integer getMaxPossibleRisk();

}
