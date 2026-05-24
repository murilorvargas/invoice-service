package com.invoice.springinvoiceservice.exceptions.customexceptions;

import com.invoice.springinvoiceservice.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class CardEntryNotFoundException extends BusinessException {

    public CardEntryNotFoundException() {
        super(
            HttpStatus.NOT_FOUND,
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            "INV00001",
            "The requested card entry could not be found."
        );
    }
}
