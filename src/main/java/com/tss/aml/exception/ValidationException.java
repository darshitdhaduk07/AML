package com.tss.aml.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class ValidationException extends ApplicationException {

    private final String field;
    private final Object value;
    private final String errorCode;
    private final int row;

    public ValidationException(String field, Object value, String errorCode, String message, int row) {
        super(message, HttpStatus.BAD_REQUEST);
        this.field = field;
        this.value = value;
        this.errorCode = errorCode;
        this.row = row;
    }
}