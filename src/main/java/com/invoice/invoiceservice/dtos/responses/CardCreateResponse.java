package com.invoice.invoiceservice.dtos.responses;

import com.invoice.invoiceservice.entities.Card;

public record CardCreateResponse(
    String cardKey,
    String requestControlKey,
    String documentNumber,
    String status
) {

    public static CardCreateResponse from(Card card) {
        return new CardCreateResponse(
            card.getCardKey(),
            card.getRequestControlKey(),
            card.getDocumentNumber(),
            card.getCardStatus().getEnumerator()
        );
    }
}
