package com.invoice.springinvoiceservice.exceptions.customexceptions;

import com.invoice.springinvoiceservice.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class CardEntryNotInProcessingConclusionStatusException extends BusinessException {

    public CardEntryNotInProcessingConclusionStatusException() {
        super(
            HttpStatus.UNPROCESSABLE_CONTENT,
            HttpStatus.UNPROCESSABLE_CONTENT.getReasonPhrase(),
            "INV00007",
            "The card entry is not in processing conclusion status."
        );
    }
}
