package com.invoice.invoiceservice.dtos.responses;

import com.invoice.invoiceservice.entities.CardEntry;

import java.math.BigDecimal;

public record CardEntryCreateResponse(
    String cardEntryKey,
    String requestControlKey,
    BigDecimal amount,
    String cardEntryType,
    String cardEntryStatus
) {

    public static CardEntryCreateResponse from(CardEntry cardEntry) {
        return new CardEntryCreateResponse(
            cardEntry.getCardEntryKey(),
            cardEntry.getRequestControlKey(),
            cardEntry.getAmount(),
            cardEntry.getCardEntryType().getEnumerator(),
            cardEntry.getCardEntryStatus().getEnumerator()
        );
    }
}
