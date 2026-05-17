package com.invoice.springinvoiceservice.dtos.responses;

import com.invoice.springinvoiceservice.entities.Wallet;
import com.invoice.springinvoiceservice.entities.WalletLimit;

import java.util.List;
import java.util.stream.Collectors;

public record WalletResponse(
    String walletKey,
    String requestControlKey,
    String documentNumber,
    String status,
    InvoiceConfigurationResponse invoiceConfiguration,
    List<WalletLimitResponse> walletLimits
) {

    public static WalletResponse from(Wallet wallet, List<WalletLimit> walletLimits) {
        InvoiceConfigurationResponse invoiceConfigurationResponse = new InvoiceConfigurationResponse(
            wallet.getInvoiceConfiguration().getInvoiceConfigurationKey(),
            wallet.getInvoiceConfiguration().getClosingFixedDay(),
            wallet.getInvoiceConfiguration().getDueFixedDay(),
            wallet.getInvoiceConfiguration().getDueOffsetMonths(),
            wallet.getInvoiceConfiguration().getDueDaysAfterClosing(),
            wallet.getInvoiceConfiguration().getFinePercentage(),
            wallet.getInvoiceConfiguration().getInterestPercentage(),
            wallet.getInvoiceConfiguration().getRevolvingInterestPercentage(),
            wallet.getInvoiceConfiguration().getDueType().getEnumerator()
        );

        List<WalletLimitResponse> walletLimitResponses = walletLimits.stream()
            .map(walletLimit -> new WalletLimitResponse(
                walletLimit.getWalletLimitKey(),
                walletLimit.getLimitAmount(),
                walletLimit.getUsedLimitAmount()
            ))
            .collect(Collectors.toList());

        return new WalletResponse(
            wallet.getWalletKey(),
            wallet.getRequestControlKey(),
            wallet.getDocumentNumber(),
            wallet.getWalletStatus().getEnumerator(),
            invoiceConfigurationResponse,
            walletLimitResponses
        );
    }
}
