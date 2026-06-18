package com.tss.aml.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends ApplicationException{
    public BusinessException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
