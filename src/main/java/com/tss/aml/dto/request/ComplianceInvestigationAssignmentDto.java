package com.tss.aml.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class ComplianceInvestigationAssignmentDto {
    private String customerNumber;
    private UUID complianceOfficerId;
}
