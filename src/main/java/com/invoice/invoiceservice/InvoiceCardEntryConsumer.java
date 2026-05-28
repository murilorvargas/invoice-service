package com.invoice.invoiceservice;

import com.invoice.invoiceservice.dtos.messages.CardEntryConclusionMessage;
import com.invoice.invoiceservice.exceptions.customexceptions.CardEntryNotInProcessingConclusionStatusException;
import com.invoice.invoiceservice.services.CardEntryService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@Component
public class InvoiceCardEntryConsumer {

    private static final String INVOICE_CARD_ENTRY_CONSUMER = "invoice-card_entry-consumer";

    private final CardEntryService cardEntryService;

    private final ObjectMapper objectMapper;

    public InvoiceCardEntryConsumer(CardEntryService cardEntryService, ObjectMapper objectMapper) {
        this.cardEntryService = cardEntryService;
        this.objectMapper = objectMapper;
    }

    private CardEntryConclusionMessage parseMessage(String rawMessage) {
        Map<String, String> envelope = objectMapper.readValue(rawMessage, new TypeReference<>() {});
        return objectMapper.readValue(envelope.get("Message"), CardEntryConclusionMessage.class);
    }

    @SqsListener(value = INVOICE_CARD_ENTRY_CONSUMER)
    public void receiveMessage(String rawMessage) {
        log.info("InvoiceCardEntryConsumer.receiveMessage - received message: {}", rawMessage);
        try {
            CardEntryConclusionMessage cardEntryConclusionMessage = parseMessage(rawMessage);
            cardEntryService.processCardEntryConclusion(cardEntryConclusionMessage);
            log.info("InvoiceCardEntryConsumer.receiveMessage - message processed successfully");
        } catch (CardEntryNotInProcessingConclusionStatusException e) {
            log.warn("InvoiceCardEntryConsumer.receiveMessage - card entry not in valid status for conclusion processing: {}", e.getMessage());
        } catch (Exception e) {
            log.error("InvoiceCardEntryConsumer.receiveMessage - unexpected error processing message", e);
            throw e;
        }
    }
}
