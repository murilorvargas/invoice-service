package com.invoice.springinvoiceservice.exceptions.customexceptions;

import com.invoice.springinvoiceservice.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class CardNotActiveException extends BusinessException {

    public CardNotActiveException() {
        super(
            HttpStatus.UNPROCESSABLE_CONTENT,
            HttpStatus.UNPROCESSABLE_CONTENT.getReasonPhrase(),
            "INV00004",
            "The requested card is not active."
        );
    }
}
