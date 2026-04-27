package com.tss.aml.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Object identifier) {
        super(resource + " With Identifier: " + identifier + " Not found.");
    }
}
