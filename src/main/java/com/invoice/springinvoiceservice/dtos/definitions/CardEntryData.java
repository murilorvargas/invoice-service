package com.invoice.springinvoiceservice.dtos.definitions;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CardEntryData {

    @NotBlank(message = "Merchant name is required")
    private String merchantName;
}
