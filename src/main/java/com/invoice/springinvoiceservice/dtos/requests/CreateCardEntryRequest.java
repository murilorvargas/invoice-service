package com.invoice.springinvoiceservice.dtos.requests;

import com.invoice.springinvoiceservice.dtos.definitions.CardEntryData;
import com.invoice.springinvoiceservice.dtos.definitions.CardEntryType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCardEntryRequest {

    @NotBlank(message = "Request control key is required")
    @Size(min = 36, max = 36, message = "Request control key must be exactly 36 characters")
    private String requestControlKey;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Digits(integer = 17, fraction = 2, message = "Amount must have at most 17 integer digits and 2 decimal places")
    private BigDecimal amount;

    @NotNull(message = "Number of installments is required")
    @Min(value = 1, message = "Number of installments must be at least 1")
    private Integer numberOfInstallments;

    @NotNull(message = "Card entry type is required")
    private CardEntryType cardEntryType;

    @NotNull(message = "Card entry data is required")
    private CardEntryData cardEntryData;
}
