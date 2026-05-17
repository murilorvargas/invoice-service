package com.invoice.springinvoiceservice.exceptions.customexceptions;

import com.invoice.springinvoiceservice.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class WalletNotActiveException extends BusinessException {

    public WalletNotActiveException() {
        super(
            HttpStatus.UNPROCESSABLE_CONTENT,
            HttpStatus.UNPROCESSABLE_CONTENT.getReasonPhrase(),
            "INV00002",
            "The requested wallet is not active."
        );
    }
}
