package com.invoice.springinvoiceservice.dtos.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CardEntryConclusionMessage extends CardEntryMessage {

    private final String cardEntryKey;

    public CardEntryConclusionMessage(String cardEntryKey) {
        super(CardEntryEventTypeEnum.CARD_ENTRY_CONCLUSION);
        this.cardEntryKey = cardEntryKey;
    }
}