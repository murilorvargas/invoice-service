package com.invoice.invoiceservice.dtos.responses.commons;

import com.invoice.invoiceservice.entities.InvoiceItem;

import java.math.BigDecimal;

public record InvoiceItemResponse(
    String invoiceItemKey,
    BigDecimal amount,
    String description,
    String invoiceItemStatus
) {

    public static InvoiceItemResponse from(InvoiceItem invoiceItem) {
        return new InvoiceItemResponse(
            invoiceItem.getInvoiceItemKey(),
            invoiceItem.getAmount(),
            invoiceItem.getDescription(),
            invoiceItem.getInvoiceItemStatus().getEnumerator()
        );
    }
}
