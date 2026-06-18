package com.tss.aml.exception;

import org.springframework.http.HttpStatus;

public class CsvParseException extends ApplicationException {
    public CsvParseException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}