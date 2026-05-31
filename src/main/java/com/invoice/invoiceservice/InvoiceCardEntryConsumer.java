package com.invoice.invoiceservice;

import com.invoice.invoiceservice.dtos.messages.CardEntryConclusionMessage;
import com.invoice.invoiceservice.dtos.messages.CardEntryEventTypeEnum;
import com.invoice.invoiceservice.dtos.messages.CardEntryMessage;
import com.invoice.invoiceservice.exceptions.customexceptions.CardEntryNotInProcessingConclusionStatusException;
import com.invoice.invoiceservice.services.CardEntryService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@Component
@Profile("consumer")
public class InvoiceCardEntryConsumer {

    private static final String INVOICE_CARD_ENTRY_CONSUMER = "invoice-card_entry-consumer";

    private final CardEntryService cardEntryService;

    private final ObjectMapper objectMapper;

    public InvoiceCardEntryConsumer(CardEntryService cardEntryService, ObjectMapper objectMapper) {
        this.cardEntryService = cardEntryService;
        this.objectMapper = objectMapper;
    }

    private CardEntryMessage parseMessage(String rawMessage) {
        Map<String, Object> envelope = objectMapper.readValue(rawMessage, new TypeReference<>() {});
        return objectMapper.readValue((String) envelope.get("Message"), CardEntryMessage.class);
    }

    @SqsListener(value = INVOICE_CARD_ENTRY_CONSUMER)
    public void receiveMessage(String rawMessage) {
        log.info("InvoiceCardEntryConsumer.receiveMessage - received message: {}", rawMessage);
        try {
            CardEntryMessage cardEntryMessage = parseMessage(rawMessage);
            if (cardEntryMessage.getEventType() == CardEntryEventTypeEnum.CARD_ENTRY_CONCLUSION) {
                cardEntryService.processCardEntryConclusion((CardEntryConclusionMessage) cardEntryMessage);
            } else {
                throw new IllegalArgumentException("Unsupported card entry event type: " + cardEntryMessage.getEventType().name());
            }
            log.info("InvoiceCardEntryConsumer.receiveMessage - message processed successfully");
        } catch (CardEntryNotInProcessingConclusionStatusException e) {
            log.warn("InvoiceCardEntryConsumer.receiveMessage - card entry not in valid status for conclusion processing: {}", e.getMessage());
        } catch (Exception e) {
            log.error("InvoiceCardEntryConsumer.receiveMessage - unexpected error processing message", e);
            throw e;
        }
    }
}
