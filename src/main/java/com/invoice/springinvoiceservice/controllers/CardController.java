package com.invoice.springinvoiceservice.controllers;

import com.invoice.springinvoiceservice.dtos.requests.CreateCardRequest;
import com.invoice.springinvoiceservice.dtos.responses.CardResponse;
import com.invoice.springinvoiceservice.services.CardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/wallets/{walletKey}/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardResponse> createCard(
        @RequestHeader("SELECTED-USER") String requesterKey,
        @PathVariable("walletKey") String walletKey,
        @Valid @RequestBody CreateCardRequest createCardRequest
    ) {
        CardResponse createCardResponse = cardService.createCard(requesterKey, walletKey, createCardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createCardResponse);
    }
}
