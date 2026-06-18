package com.tss.aml.dto.request;

import lombok.Data;

@Data
public class CaseRequestDto {
    private String caseName;
    private String caseDescription;
    private String customerNumber;
}
