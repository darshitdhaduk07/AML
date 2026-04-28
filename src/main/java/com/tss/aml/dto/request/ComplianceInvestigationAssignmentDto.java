package com.tss.aml.dto.request;

import com.tss.aml.tenant.entity.ComplianceOfficer;
import lombok.Data;

import java.util.UUID;

@Data
public class ComplianceInvestigationAssignmentDto {
    private String customerNumber;
    private UUID complianceOfficerId;
}
