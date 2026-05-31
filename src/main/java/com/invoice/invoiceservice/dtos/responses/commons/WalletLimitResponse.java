package com.invoice.invoiceservice.dtos.responses.commons;

import com.invoice.invoiceservice.entities.WalletLimit;

import java.math.BigDecimal;

public record WalletLimitResponse(
    String walletLimitKey,
    BigDecimal limitAmount,
    BigDecimal usedLimitAmount
) {

    public static WalletLimitResponse from(WalletLimit walletLimit) {
        return new WalletLimitResponse(
            walletLimit.getWalletLimitKey(),
            walletLimit.getLimitAmount(),
            walletLimit.getUsedLimitAmount()
        );
    }
}
