package com.invoice.springinvoiceservice.dtos.validators.dueconfiguration;

import com.invoice.springinvoiceservice.dtos.definitions.DueType;
import com.invoice.springinvoiceservice.dtos.definitions.InvoiceConfiguration;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DueConfigurationValidator implements ConstraintValidator<DueConfiguration, InvoiceConfiguration> {

    @Override
    public boolean isValid(InvoiceConfiguration invoiceConfiguration, ConstraintValidatorContext context) {
        if (invoiceConfiguration == null || invoiceConfiguration.getDueType() == null) return true;

        if (invoiceConfiguration.getDueType() == DueType.FIXED_DAY) {
            return invoiceConfiguration.getDueFixedDay() != null && invoiceConfiguration.getDueOffsetMonths() != null;
        }
        if (invoiceConfiguration.getDueType() == DueType.RULE) {
            return invoiceConfiguration.getDueDaysAfterClosing() != null;
        }
        return true;
    }
}
