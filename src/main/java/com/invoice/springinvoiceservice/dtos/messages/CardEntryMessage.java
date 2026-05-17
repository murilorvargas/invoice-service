package com.invoice.springinvoiceservice.dtos.messages;

import lombok.Data;

@Data
public abstract class CardEntryMessage {

    public static final String TOPIC_NAME = "card-entry-topic";

    private final CardEntryEventTypeEnum eventType;

    protected CardEntryMessage(CardEntryEventTypeEnum eventType) {
        this.eventType = eventType;
    }
}
