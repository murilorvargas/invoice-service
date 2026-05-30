package com.invoice.invoiceservice.dtos.requests.commons;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Person {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
    private String name;

    @NotBlank(message = "Document number is required")
    @Size(min = 11, max = 14, message = "Document number must be between 11 and 14 characters")
    private String documentNumber;
}
