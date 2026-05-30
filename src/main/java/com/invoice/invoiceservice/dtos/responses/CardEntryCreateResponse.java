package com.invoice.invoiceservice.dtos.responses;

import java.math.BigDecimal;

public record CardEntryCreateResponse(
    String cardEntryKey,
    String requestControlKey,
    BigDecimal amount,
    String cardEntryType,
    String cardEntryStatus
) {

    public static CardEntryCreateResponse from(
        String cardEntryKey,
        String requestControlKey,
        BigDecimal amount,
        String cardEntryType,
        String cardEntryStatus
    ) {
        return new CardEntryCreateResponse(
            cardEntryKey,
            requestControlKey,
            amount,
            cardEntryType,
            cardEntryStatus
        );
    }
}
