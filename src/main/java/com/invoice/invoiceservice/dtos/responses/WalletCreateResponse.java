package com.invoice.invoiceservice.dtos.responses;

import com.invoice.invoiceservice.dtos.responses.commons.InvoiceConfigurationResponse;
import com.invoice.invoiceservice.dtos.responses.commons.WalletLimitResponse;
import com.invoice.invoiceservice.entities.Wallet;
import com.invoice.invoiceservice.entities.WalletLimit;

import java.util.List;

public record WalletCreateResponse(
    String walletKey,
    String requestControlKey,
    String documentNumber,
    String walletStatus,
    InvoiceConfigurationResponse invoiceConfiguration,
    List<WalletLimitResponse> walletLimits
) {

    public static WalletCreateResponse from(Wallet wallet, List<WalletLimit> walletLimits) {
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

        return new WalletCreateResponse(
            wallet.getWalletKey(),
            wallet.getRequestControlKey(),
            wallet.getDocumentNumber(),
            wallet.getWalletStatus().getEnumerator(),
            invoiceConfigurationResponse,
            walletLimits.stream().map(WalletLimitResponse::from).toList()
        );
    }
}
