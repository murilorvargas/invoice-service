package com.invoice.springinvoiceservice.dtos.responses;

import java.math.BigDecimal;

public record InvoiceConfigurationResponse(
    String invoiceConfigurationKey,
    Integer closingFixedDay,
    Integer dueFixedDay,
    Integer dueOffsetMonths,
    Integer dueDaysAfterClosing,
    BigDecimal finePercentage,
    BigDecimal interestPercentage,
    BigDecimal revolvingInterestPercentage,
    String dueType
) {}
