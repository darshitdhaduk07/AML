package com.tss.aml.tenant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "batch_summaries")
@Getter
@Setter
public class BatchSummary extends BaseEntity {

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType; // e.g., "CUSTOMER" or "TRANSACTION"

    @Column(nullable = false)
    private long recordCount;

    @Column(nullable = false)
    private String status; // e.g., "SUCCESS", "FAILED"

    @Column(length = 1000)
    private String errorMessage;

    @Column(columnDefinition = "TEXT")
    private String details; // Stores detailed JSON error list
}
