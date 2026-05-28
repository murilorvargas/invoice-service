package com.invoice.invoiceservice.dtos.responses;

import com.invoice.invoiceservice.entities.Card;

public record CardResponse(
    String cardKey,
    String requestControlKey,
    String documentNumber,
    String status
) {

    public static CardResponse from(Card card) {
        return new CardResponse(
            card.getCardKey(),
            card.getRequestControlKey(),
            card.getDocumentNumber(),
            card.getCardStatus().getEnumerator()
        );
    }
}
