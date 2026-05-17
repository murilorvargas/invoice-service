package com.invoice.springinvoiceservice.dtos.requests;

import com.invoice.springinvoiceservice.dtos.definitions.InvoiceConfiguration;
import com.invoice.springinvoiceservice.dtos.definitions.Person;
import com.invoice.springinvoiceservice.dtos.definitions.WalletLimit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateWalletRequest {

    @NotBlank(message = "Request control key is required")
    @Size(min = 36, max = 36, message = "Request control key must be exactly 36 characters")
    private String requestControlKey;

    @NotNull(message = "Owner is required")
    private Person owner;

    @NotNull(message = "Invoice configuration is required")
    private InvoiceConfiguration invoiceConfiguration;

    @NotNull(message = "Wallet limit is required")
    private WalletLimit walletLimit;
}
