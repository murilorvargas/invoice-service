package com.invoice.invoiceservice.exceptions.customexceptions;

import com.invoice.invoiceservice.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class CardNotFoundException extends BusinessException {

    public CardNotFoundException() {
        super(
            HttpStatus.NOT_FOUND,
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            "INV00001",
            "The requested card could not be found."
        );
    }
}
