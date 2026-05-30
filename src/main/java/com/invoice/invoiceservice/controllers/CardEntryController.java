package com.invoice.invoiceservice.controllers;

import com.invoice.invoiceservice.dtos.requests.CreateCardEntryRequest;
import com.invoice.invoiceservice.dtos.responses.CardEntryCreateResponse;
import com.invoice.invoiceservice.services.CardEntryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets/{walletKey}/cards/{cardKey}/card_entries")
public class CardEntryController {

    private final CardEntryService cardEntryService;

    public CardEntryController(CardEntryService cardEntryService) {
        this.cardEntryService = cardEntryService;
    }

    @PostMapping
    public ResponseEntity<CardEntryCreateResponse> createCardEntry(
        @RequestHeader("SELECTED-USER") String requesterKey,
        @PathVariable String walletKey,
        @PathVariable String cardKey,
        @Valid @RequestBody CreateCardEntryRequest createCardEntryRequest
    ) {
        CardEntryCreateResponse cardEntryResponse = cardEntryService.createCardEntry(
            requesterKey,
            walletKey,
            cardKey,
            createCardEntryRequest
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(cardEntryResponse);
    }
}
