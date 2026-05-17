package com.invoice.springinvoiceservice.dtos.messages;

import lombok.Data;

@Data
public abstract class CardEntryMessage {

    private final CardEntryEventTypeEnum eventType;

    protected CardEntryMessage(CardEntryEventTypeEnum eventType) {
        this.eventType = eventType;
    }
}
