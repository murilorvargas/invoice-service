package com.invoice.invoiceservice.controllers;

import com.invoice.invoiceservice.dtos.requests.CreateWalletRequest;
import com.invoice.invoiceservice.dtos.responses.commons.PaginationResponse;
import com.invoice.invoiceservice.dtos.responses.WalletCreateResponse;
import com.invoice.invoiceservice.dtos.responses.WalletGetByKeyResponse;
import com.invoice.invoiceservice.dtos.responses.WalletGetResponse;
import com.invoice.invoiceservice.services.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<WalletCreateResponse> createWallet(
        @RequestHeader("SELECTED-USER") String requesterKey,
        @Valid @RequestBody CreateWalletRequest createWalletRequest
    ) {
        WalletCreateResponse createdWalletResponse = walletService.createWallet(requesterKey, createWalletRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWalletResponse);
    }

    @GetMapping
    public ResponseEntity<PaginationResponse<WalletGetResponse>> getWallets(
        @RequestHeader("SELECTED-USER") String requesterKey,
        @RequestParam(required = false) String walletKey,
        @RequestParam(required = false) String requestControlKey,
        @RequestParam(required = false) String documentNumber,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "30") int pageSize
    ) {
        PaginationResponse<WalletGetResponse> walletResponses = walletService.getWallets(requesterKey, walletKey, requestControlKey, documentNumber, page, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(walletResponses);
    }

    @GetMapping("/{walletKey}")
    public ResponseEntity<WalletGetByKeyResponse> getWalletByKey(
        @RequestHeader("SELECTED-USER") String requesterKey,
        @PathVariable String walletKey
    ) {
        WalletGetByKeyResponse walletResponse = walletService.getWalletByKey(requesterKey, walletKey);
        return ResponseEntity.status(HttpStatus.OK).body(walletResponse);
    }
}
