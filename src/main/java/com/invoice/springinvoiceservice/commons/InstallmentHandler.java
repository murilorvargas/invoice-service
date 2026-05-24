package com.invoice.springinvoiceservice.commons;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class InstallmentHandler {

    public static List<BigDecimal> calculateInstallmentAmounts(BigDecimal totalAmount, int numberOfInstallments) {
        if (numberOfInstallments == 1) {
            return List.of(totalAmount);
        }

        BigDecimal baseInstallmentAmount = totalAmount
            .divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.DOWN);

        BigDecimal firstInstallmentAmount = totalAmount
            .subtract(baseInstallmentAmount.multiply(BigDecimal.valueOf(numberOfInstallments - 1)));

        List<BigDecimal> installmentAmounts = new ArrayList<>();
        installmentAmounts.add(firstInstallmentAmount);
        for (int i = 0; i < numberOfInstallments - 1; i++) {
            installmentAmounts.add(baseInstallmentAmount);
        }

        return installmentAmounts;
    }
}
