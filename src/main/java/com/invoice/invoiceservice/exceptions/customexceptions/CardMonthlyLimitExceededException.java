package com.invoice.invoiceservice.exceptions.customexceptions;

import com.invoice.invoiceservice.exceptions.BusinessException;
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
