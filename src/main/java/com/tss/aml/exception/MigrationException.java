package com.tss.aml.exception;

import org.springframework.http.HttpStatus;

public class MigrationException extends ApplicationException{
    public MigrationException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
