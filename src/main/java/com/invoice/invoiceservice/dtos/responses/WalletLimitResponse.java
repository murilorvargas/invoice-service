package com.invoice.invoiceservice.dtos.responses;

import java.math.BigDecimal;

public record WalletLimitResponse(
    String walletLimitKey,
    BigDecimal limitAmount,
    BigDecimal usedLimitAmount
) {}
