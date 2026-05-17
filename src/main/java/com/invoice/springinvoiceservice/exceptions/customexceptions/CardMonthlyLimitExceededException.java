package com.invoice.springinvoiceservice.exceptions.customexceptions;

import com.invoice.springinvoiceservice.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class CardMonthlyLimitExceededException extends BusinessException {

    public CardMonthlyLimitExceededException() {
        super(
            HttpStatus.UNPROCESSABLE_CONTENT,
            HttpStatus.UNPROCESSABLE_CONTENT.getReasonPhrase(),
            "INV00005",
            "The card monthly limit amount has been exceeded."
        );
    }
}
