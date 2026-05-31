package com.invoice.invoiceservice.controllers;

import com.invoice.invoiceservice.dtos.requests.CreateCardEntryRequest;
import com.invoice.invoiceservice.dtos.responses.CardEntryCreateResponse;
import com.invoice.invoiceservice.dtos.responses.CardEntryGetResponse;
import com.invoice.invoiceservice.dtos.responses.commons.PaginationResponse;
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

    @GetMapping
    public ResponseEntity<PaginationResponse<CardEntryGetResponse>> getCardEntries(
        @RequestHeader("SELECTED-USER") String requesterKey,
        @PathVariable String walletKey,
        @PathVariable String cardKey,
        @RequestParam(required = false) String cardEntryKey,
        @RequestParam(required = false) String requestControlKey,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        PaginationResponse<CardEntryGetResponse> cardEntryResponses = cardEntryService.getCardEntries(
            requesterKey,
            walletKey,
            cardKey,
            cardEntryKey,
            requestControlKey,
            page,
            size
        );
        return ResponseEntity.status(HttpStatus.OK).body(cardEntryResponses);
    }
}
