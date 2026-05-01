package com.tss.aml.dto.result;

import com.tss.aml.enums.TenantStatus;
import lombok.Data;

@Data
public class TenantResponseDto {
    private String tenantName;
    private TenantStatus tenantStatus;
}
