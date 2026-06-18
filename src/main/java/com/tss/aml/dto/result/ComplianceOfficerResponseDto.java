package com.tss.aml.dto.result;

import lombok.Data;

@Data
public class ComplianceOfficerResponseDto {
    private String email;
    private Boolean isSuspended;
    private Boolean isLocked;
}
