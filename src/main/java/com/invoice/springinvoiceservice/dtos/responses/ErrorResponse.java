package com.invoice.springinvoiceservice.dtos.responses;

public record ErrorResponse(
    String title,
    String code,
    String message
) {}
