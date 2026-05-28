package com.invoice.invoiceservice.controllers;

import com.invoice.invoiceservice.dtos.requests.CreateWalletRequest;
import com.invoice.invoiceservice.dtos.responses.WalletResponse;
import com.invoice.invoiceservice.services.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController("/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(
        @RequestHeader("SELECTED-USER") String requesterKey,
        @Valid @RequestBody CreateWalletRequest createWalletRequest
    ) {
        WalletResponse createdWalletResponse = walletService.createWallet(requesterKey, createWalletRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWalletResponse);
    }
}
