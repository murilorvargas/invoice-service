package com.invoice.invoiceservice.dtos.requests;

import com.invoice.invoiceservice.dtos.requests.commons.Person;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCardRequest {

    @NotBlank(message = "Request control key is required")
    @Size(min = 36, max = 36, message = "Request control key must be exactly 36 characters")
    private String requestControlKey;

    private Person owner;

    @Min(value = 0, message = "Card monthly limit amount must be non-negative")
    @Digits(integer = 17, fraction = 2, message = "Monthly limit amount must have at most 17 integer digits and 2 decimal places")
    private BigDecimal monthlyLimitAmount;
}
