package com.invoice.invoiceservice.exceptions.customexceptions;

import com.invoice.invoiceservice.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class WalletNotFoundException extends BusinessException {

    public WalletNotFoundException() {
        super(
            HttpStatus.NOT_FOUND,
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            "INV00001",
            "The requested wallet could not be found."
        );
    }
}
