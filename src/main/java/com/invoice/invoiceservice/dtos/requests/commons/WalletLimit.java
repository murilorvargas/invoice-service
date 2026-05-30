package com.invoice.invoiceservice.dtos.requests.commons;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletLimit {

    @NotNull(message = "Limit amount is required")
    @Min(value = 0, message = "Limit amount must be non-negative")
    @Digits(integer = 17, fraction = 2, message = "Limit amount must have at most 17 integer digits and 2 decimal places")
    private BigDecimal limitAmount;
}
