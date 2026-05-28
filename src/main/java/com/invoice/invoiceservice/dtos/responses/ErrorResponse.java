package com.invoice.invoiceservice.dtos.responses;

public record ErrorResponse(
    String title,
    String code,
    String message
) {}
