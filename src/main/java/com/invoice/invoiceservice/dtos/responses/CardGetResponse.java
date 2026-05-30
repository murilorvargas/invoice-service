package com.invoice.invoiceservice.dtos.responses;

import com.invoice.invoiceservice.entities.Card;

public record CardGetResponse(
    String cardKey,
    String requestControlKey,
    String documentNumber,
    String status
) {

    public static CardGetResponse from(Card card) {
        return new CardGetResponse(
            card.getCardKey(),
            card.getRequestControlKey(),
            card.getDocumentNumber(),
            card.getCardStatus().getEnumerator()
        );
    }
}
