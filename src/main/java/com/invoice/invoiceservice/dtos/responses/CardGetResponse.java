package com.invoice.invoiceservice.dtos.responses;

import com.invoice.invoiceservice.entities.Card;

import java.math.BigDecimal;

public record CardGetResponse(
    String cardKey,
    String requestControlKey,
    String documentNumber,
    BigDecimal monthlyLimitAmount,
    BigDecimal usedMonthlyLimitAmount,
    String cardStatus
) {

    public static CardGetResponse from(Card card) {
        return new CardGetResponse(
            card.getCardKey(),
            card.getRequestControlKey(),
            card.getDocumentNumber(),
            card.getMonthlyLimitAmount(),
            card.getUsedMonthlyLimitAmount(),
            card.getCardStatus().getEnumerator()
        );
    }
}
