package com.tss.aml.model;

import lombok.Data;

@Data
public class ParameterMeta {

    private final Class<?> type;
    private final boolean required;
    private final boolean positive;

    public ParameterMeta(Class<?> type, boolean required, boolean positive) {
        this.type = type;
        this.required = required;
        this.positive = positive;
    }

    public boolean validate(Object value) {

        if (required && value == null) {
            return false;
        }

        if (value == null) {
            return true;
        }

        if (!type.isInstance(value)) {
            return false;
        }

        if (positive && value instanceof Number number) {
            return number.doubleValue() > 0;
        }

        return true;
    }
}