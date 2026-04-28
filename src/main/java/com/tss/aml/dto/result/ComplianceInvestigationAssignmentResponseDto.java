package com.tss.aml.dto.result;

import com.tss.aml.tenant.entity.Customer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ComplianceInvestigationAssignmentResponseDto {
    private Customer customer;
    private BigDecimal riskScore;
    private Boolean isOpen;
}
