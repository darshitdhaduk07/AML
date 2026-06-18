package com.tss.aml.dto.request;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
    private String tenant;
    private String role;
}