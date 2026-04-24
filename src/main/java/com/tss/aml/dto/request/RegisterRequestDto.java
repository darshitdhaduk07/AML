package com.tss.aml.dto.request;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String tenantName;
    private String email;
}