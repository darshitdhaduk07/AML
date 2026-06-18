package com.tss.aml.dto.result;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ComplianceInvestigationAssignmentResponseDto {
    private CustomerResponseDto customerResponseDto;
    private BigDecimal riskScore;
    private Boolean isOpen;
    private List<AlertResponseDto> alerts;
}
