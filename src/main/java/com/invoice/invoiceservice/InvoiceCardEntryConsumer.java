package com.invoice.invoiceservice;

import com.invoice.invoiceservice.services.CardEntryService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class InvoiceCardEntryConsumer {

    private static final String INVOICE_CARD_ENTRY_CONSUMER = "invoice-card_entry-consumer";

    private final CardEntryService cardEntryService;

    public InvoiceCardEntryConsumer(CardEntryService cardEntryService) {
        this.cardEntryService = cardEntryService;
    }

    @SqsListener(value = INVOICE_CARD_ENTRY_CONSUMER)
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
    }

    private void receiveCardEntryMessage(String message) {
    }
}
