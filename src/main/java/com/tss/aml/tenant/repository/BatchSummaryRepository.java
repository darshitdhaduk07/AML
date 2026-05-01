package com.tss.aml.tenant.repository;

import com.tss.aml.tenant.entity.BatchSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface BatchSummaryRepository extends JpaRepository<BatchSummary, UUID> {
    List<BatchSummary> findAllByOrderByCreatedAtDesc();
}
