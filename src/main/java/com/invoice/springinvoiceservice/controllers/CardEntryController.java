package com.invoice.springinvoiceservice.controllers;

import com.invoice.springinvoiceservice.dtos.requests.CreateCardEntryRequest;
import com.invoice.springinvoiceservice.dtos.responses.CardEntryResponse;
import com.invoice.springinvoiceservice.services.CardEntryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/wallets/{walletKey}/cards/{cardKey}/card_entries")
public class CardEntryController {

    private final CardEntryService cardEntryService;

    public CardEntryController(CardEntryService cardEntryService) {
        this.cardEntryService = cardEntryService;
    }

    @PostMapping
    public ResponseEntity<CardEntryResponse> createCardEntry(
        @RequestHeader("SELECTED-USER") String requesterKey,
        @PathVariable("walletKey") String walletKey,
        @PathVariable("cardKey") String cardKey,
        @Valid @RequestBody CreateCardEntryRequest createCardEntryRequest
    ) {
        CardEntryResponse cardEntryResponse = cardEntryService.createCardEntry(
            requesterKey,
            walletKey,
            cardKey,
            createCardEntryRequest
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(cardEntryResponse);
    }
}
