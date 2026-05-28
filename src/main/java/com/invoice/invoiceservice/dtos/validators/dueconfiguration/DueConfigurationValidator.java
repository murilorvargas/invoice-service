package com.invoice.invoiceservice.dtos.validators.dueconfiguration;

import com.invoice.invoiceservice.dtos.definitions.DueType;
import com.invoice.invoiceservice.dtos.definitions.InvoiceConfiguration;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DueConfigurationValidator implements ConstraintValidator<DueConfiguration, InvoiceConfiguration> {

    @Override
    public boolean isValid(InvoiceConfiguration invoiceConfiguration, ConstraintValidatorContext context) {
        if (invoiceConfiguration == null || invoiceConfiguration.getDueType() == null) return true;

        if (invoiceConfiguration.getDueType() == DueType.FIXED_DAY) {
            if (invoiceConfiguration.getDueFixedDay() == null || invoiceConfiguration.getDueOffsetMonths() == null) {
                return false;
            }

            int closingFixedDay = invoiceConfiguration.getClosingFixedDay();
            int dueFixedDay = invoiceConfiguration.getDueFixedDay();
            int dueOffsetMonths = invoiceConfiguration.getDueOffsetMonths();

            int minGapInDays = dueOffsetMonths == 0
                ? dueFixedDay - closingFixedDay
                : (28 - closingFixedDay) + dueFixedDay;

            return minGapInDays >= 3;
        }
        if (invoiceConfiguration.getDueType() == DueType.RULE) {
            return invoiceConfiguration.getDueDaysAfterClosing() != null;
        }
        return true;
    }
}
