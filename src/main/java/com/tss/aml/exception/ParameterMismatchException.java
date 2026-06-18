package com.tss.aml.exception;

public class ParameterMismatchException extends RuntimeException {
    public ParameterMismatchException() {
        super("Parameter of these rule doesn't match the Rule Template requirement.");
    }
}
