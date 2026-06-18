package com.tss.aml.exception;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class BulkValidationException extends RuntimeException {

    private final List<ValidationException> errors;

    public BulkValidationException(List<ValidationException> errors) {
        super("Multiple validation errors occurred");
        this.errors = errors;
    }
}