package com.tss.aml.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends ApplicationException {

    private String field;
    private Object value;
    private String errorCode;
    private int row; // optional (for CSV)

    public ValidationException(String field, Object value, String errorCode, String message, int row) {
        super(message, HttpStatus.BAD_REQUEST);
        this.field = field;
        this.value = value;
        this.errorCode = errorCode;
        this.row = row;
    }

    public String getField() { return field; }
    public Object getValue() { return value; }
    public String getErrorCode() { return errorCode; }
    public int getRow() { return row; }

    @Override
    public String toString() {
        return "ValidationException{" +
                "field='" + field + '\'' +
                ", value=" + value +
                ", errorCode='" + errorCode + '\'' +
                ", row=" + row +
                '}';
    }
}