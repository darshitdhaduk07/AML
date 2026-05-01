package com.tss.aml.dto.result;

import com.tss.aml.enums.CaseStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class CaseResponseDto {
    private UUID id;
    private String caseName;
    private String caseDescription;
    private CaseStatus caseStatus;
    private String customerNumber;
    private boolean isSarFiled;
}
