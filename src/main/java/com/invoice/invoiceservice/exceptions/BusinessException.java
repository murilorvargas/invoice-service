package com.invoice.invoiceservice.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String title;
    private final String code;
    private final String description;

    public BusinessException(HttpStatus httpStatus, String title, String code, String description) {
        super(description);
        this.httpStatus = httpStatus;
        this.title = title;
        this.code = code;
        this.description = description;
    }
}
