package com.invoice.springinvoiceservice.controllers.advicers;

import com.invoice.springinvoiceservice.dtos.responses.ErrorResponse;
import com.invoice.springinvoiceservice.exceptions.BusinessException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String constraintViolations =  ex.getConstraintViolations()
            .stream()
            .map(v -> Map.of("field", v.getPropertyPath().toString(), "message", v.getMessage()))
            .toList()
            .toString();

        ErrorResponse response = new ErrorResponse(
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "GEN00001",
            constraintViolations
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex) {
        String fieldErrors =  ex.getBindingResult().getFieldErrors()
            .stream()
            .map(err -> Map.of("field", err.getField(), "message", Objects.requireNonNullElse(err.getDefaultMessage(), "invalid value")))
            .toList()
            .toString();

        ErrorResponse response = new ErrorResponse(
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "GEN00001",
            fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse response = new ErrorResponse(
            ex.getTitle(),
            ex.getCode(),
            ex.getDescription()
        );
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }
}
