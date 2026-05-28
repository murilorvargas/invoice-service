package com.invoice.invoiceservice.dtos.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CardEntryConclusionMessage.class, name = "CARD_ENTRY_CONCLUSION")
})
public abstract class CardEntryMessage {

    public static final String TOPIC_NAME = "card_entry";

    private final CardEntryEventTypeEnum eventType;

    protected CardEntryMessage(CardEntryEventTypeEnum eventType) {
        this.eventType = eventType;
    }
}
