package com.invoice.springinvoiceservice.connectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class SnsConnector {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishMessage() {

    }
}
