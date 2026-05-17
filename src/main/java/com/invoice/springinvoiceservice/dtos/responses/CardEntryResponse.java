package com.invoice.springinvoiceservice.dtos.responses;

import java.math.BigDecimal;

public record CardEntryResponse(
    String cardEntryKey,
    String requestControlKey,
    BigDecimal amount,
    String cardEntryType,
    String cardEntryStatus
) {

    public static CardEntryResponse from(
        String cardEntryKey,
        String requestControlKey,
        BigDecimal amount,
        String cardEntryType,
        String cardEntryStatus
    ) {
        return new CardEntryResponse(
            cardEntryKey,
            requestControlKey,
            amount,
            cardEntryType,
            cardEntryStatus
        );
    }
}