package com.invoice.invoiceservice.exceptions.customexceptions;

import com.invoice.invoiceservice.exceptions.BusinessException;
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
