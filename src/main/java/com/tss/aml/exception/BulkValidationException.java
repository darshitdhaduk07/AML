package com.tss.aml.exception;

import java.util.List;

public class BulkValidationException extends RuntimeException {

    private List<ValidationException> errors;

    public BulkValidationException(List<ValidationException> errors) {
        super("Multiple validation errors occurred");
        this.errors = errors;
    }

    public List<ValidationException> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "BulkValidationException{" +
                "errors=" + errors +
                '}';
    }
}