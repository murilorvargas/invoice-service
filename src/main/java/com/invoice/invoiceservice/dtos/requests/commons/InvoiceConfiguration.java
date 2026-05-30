package com.invoice.invoiceservice.dtos.requests.commons;

import com.invoice.invoiceservice.dtos.validators.dueconfiguration.DueConfiguration;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@DueConfiguration(message = "Invalid due configuration: For FIXED_DAY, dueFixedDay and dueOffsetMonths must not be null, and the resulting due date must be at least 3 days after the closing date in any month. For RULE, dueDaysAfterClosing must not be null.")
public class InvoiceConfiguration {

    @NotNull(message = "Closing fixed day is required")
    @Min(value = 1, message = "Closing fixed day must be between 1 and 27")
    @Max(value = 27, message = "Closing fixed day must be between 1 and 27")
    private Integer closingFixedDay;

    @NotNull(message = "Due type is required")
    private DueType dueType;

    @Min(value = 1, message = "Due fixed day must be between 1 and 27")
    @Max(value = 27, message = "Due fixed day must be between 1 and 27")
    private Integer dueFixedDay;

    @Min(value = 0, message = "Due offset months must be non-negative")
    @Max(value = 1, message = "Due offset months must be 0 or 1")
    private Integer dueOffsetMonths;

    @Min(value = 3, message = "Due days after closing must be between 3 and 60")
    @Max(value = 60, message = "Due days after closing must be between 3 and 60")
    private Integer dueDaysAfterClosing;

    @NotNull(message = "Fine percentage is required")
    @Digits(integer = 1, fraction = 4, message = "Fine percentage must have up to 1 integer digit and 4 decimal places")
    @DecimalMin(value = "0.0001", message = "Fine percentage must be greater than 0")
    @DecimalMax(value = "0.9999", message = "Fine percentage must be less than 1")
    private BigDecimal finePercentage;

    @NotNull(message = "Interest percentage is required")
    @Digits(integer = 1, fraction = 4, message = "Interest percentage must have up to 1 integer digit and 4 decimal places")
    @DecimalMin(value = "0.0001", message = "Interest percentage must be greater than 0")
    @DecimalMax(value = "0.9999", message = "Interest percentage must be less than 1")
    private BigDecimal interestPercentage;

    @NotNull(message = "Revolving interest percentage is required")
    @Digits(integer = 1, fraction = 4, message = "Revolving interest percentage must have up to 1 integer digit and 4 decimal places")
    @DecimalMin(value = "0.0001", message = "Revolving interest percentage must be greater than 0")
    @DecimalMax(value = "0.9999", message = "Revolving interest percentage must be less than 1")
    private BigDecimal revolvingInterestPercentage;
}
