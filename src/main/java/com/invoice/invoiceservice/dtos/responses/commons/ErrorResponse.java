package com.invoice.invoiceservice.dtos.responses.commons;

public record ErrorResponse(
    String title,
    String code,
    String message
) {}
