package com.tss.aml.dto.request;

import lombok.Data;

@Data
public class TenantRegisterRequestDto {
    private String tenantName;
    private String email;
}