package com.tss.aml.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String message,
            HttpStatus status,
            HttpServletRequest request,
            Map<String, String> errors
    ) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(message);
        error.setStatus(status.value());
        error.setTimestamp(LocalDateTime.now());
        error.setErrors(errors);

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleException(ApplicationException applicationException, HttpServletRequest httpServletRequest){
        return buildErrorResponse(
                applicationException.getMessage(),
                applicationException.getStatus(),
                httpServletRequest,
                null
        );
    }

    @ExceptionHandler(BulkValidationException.class)
    public ResponseEntity<?> handleBulk(BulkValidationException ex) {

        List<Map<String, Object>> errorList = new ArrayList<>();

        for (ValidationException e : ex.getErrors()) {
            Map<String, Object> err = new HashMap<>();
            err.put("field", e.getField());
            err.put("value", e.getValue());
            err.put("error", e.getErrorCode());
            err.put("message", e.getMessage());
            err.put("row", e.getRow());

            errorList.add(err);
        }

        return ResponseEntity.badRequest().body(Map.of("errors", errorList));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAnyException(
            Exception exception,
            HttpServletRequest request) {

        exception.printStackTrace();
        
        return buildErrorResponse(
                "An unexpected error occurred. Please contact support.",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request,
                null
        );
    }
}
