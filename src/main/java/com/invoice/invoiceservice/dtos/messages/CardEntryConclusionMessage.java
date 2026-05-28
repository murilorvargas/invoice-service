package com.invoice.invoiceservice.dtos.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CardEntryConclusionMessage extends CardEntryMessage {

    private final String walletKey;
    private final String cardEntryKey;

    public CardEntryConclusionMessage(String walletKey, String cardEntryKey) {
        super(CardEntryEventTypeEnum.CARD_ENTRY_CONCLUSION);
        this.walletKey = walletKey;
        this.cardEntryKey = cardEntryKey;
    }
}