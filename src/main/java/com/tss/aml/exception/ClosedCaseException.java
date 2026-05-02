package com.tss.aml.exception;

import org.springframework.http.HttpStatus;

public class ClosedCaseException extends ApplicationException {
    public ClosedCaseException() {
        super("Operation cannot be performed on a closed investigation.", HttpStatus.CONFLICT);
    }
}
