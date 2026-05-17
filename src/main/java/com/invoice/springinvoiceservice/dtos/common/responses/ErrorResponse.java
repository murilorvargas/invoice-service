package com.invoice.springinvoiceservice.dtos.common.responses;

public record ErrorResponse(
    String title,
    String code,
    String message
) {}
