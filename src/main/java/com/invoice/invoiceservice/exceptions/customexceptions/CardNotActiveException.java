package com.invoice.invoiceservice.exceptions.customexceptions;

import com.invoice.invoiceservice.exceptions.BusinessException;
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
