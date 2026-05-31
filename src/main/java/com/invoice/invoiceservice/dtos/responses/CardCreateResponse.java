package com.invoice.invoiceservice.dtos.responses;

import com.invoice.invoiceservice.entities.Card;

import java.math.BigDecimal;

public record CardCreateResponse(
    String cardKey,
    String requestControlKey,
    String documentNumber,
    BigDecimal monthlyLimitAmount,
    BigDecimal usedMonthlyLimitAmount,
    String cardStatus
) {

    public static CardCreateResponse from(Card card) {
        return new CardCreateResponse(
            card.getCardKey(),
            card.getRequestControlKey(),
            card.getDocumentNumber(),
            card.getMonthlyLimitAmount(),
            card.getUsedMonthlyLimitAmount(),
            card.getCardStatus().getEnumerator()
        );
    }
}
