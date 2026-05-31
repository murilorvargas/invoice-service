package com.invoice.invoiceservice.dtos.responses;

import com.invoice.invoiceservice.dtos.responses.commons.InvoiceItemResponse;
import com.invoice.invoiceservice.entities.Invoice;
import com.invoice.invoiceservice.entities.InvoiceItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceGetResponse(
    String invoiceKey,
    LocalDate closingDate,
    LocalDate dueDate,
    BigDecimal amount,
    String invoiceStatus,
    List<InvoiceItemResponse> invoiceItems
) {

    public static InvoiceGetResponse from(Invoice invoice, List<InvoiceItem> invoiceItems) {
        return new InvoiceGetResponse(
            invoice.getInvoiceKey(),
            invoice.getClosingDate(),
            invoice.getDueDate(),
            invoice.getAmount(),
            invoice.getInvoiceStatus().getEnumerator(),
            invoiceItems.stream().map(InvoiceItemResponse::from).toList()
        );
    }
}
