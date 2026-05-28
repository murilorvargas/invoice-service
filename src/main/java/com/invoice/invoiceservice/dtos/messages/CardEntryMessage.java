package com.invoice.invoiceservice.dtos.messages;

import lombok.Data;

@Data
public abstract class CardEntryMessage {

    public static final String TOPIC_NAME = "card_entry";

    private final CardEntryEventTypeEnum eventType;

    protected CardEntryMessage(CardEntryEventTypeEnum eventType) {
        this.eventType = eventType;
    }
}
