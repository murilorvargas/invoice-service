package com.invoice.invoiceservice.dtos.responses;

import com.invoice.invoiceservice.entities.Wallet;

public record WalletGetResponse(
    String walletKey,
    String requestControlKey,
    String documentNumber,
    String walletStatus
) {

    public static WalletGetResponse from(Wallet wallet) {
        return new WalletGetResponse(
            wallet.getWalletKey(),
            wallet.getRequestControlKey(),
            wallet.getDocumentNumber(),
            wallet.getWalletStatus().getEnumerator()
        );
    }
}
