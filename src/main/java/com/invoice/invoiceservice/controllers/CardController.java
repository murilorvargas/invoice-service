package com.invoice.invoiceservice.controllers;

import com.invoice.invoiceservice.dtos.requests.CreateCardRequest;
import com.invoice.invoiceservice.dtos.responses.CardResponse;
import com.invoice.invoiceservice.dtos.responses.PaginationResponse;
import com.invoice.invoiceservice.services.CardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets/{walletKey}/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardResponse> createCard(
        @RequestHeader("SELECTED-USER") String requesterKey,
        @PathVariable String walletKey,
        @Valid @RequestBody CreateCardRequest createCardRequest
    ) {
        CardResponse createCardResponse = cardService.createCard(requesterKey, walletKey, createCardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createCardResponse);
    }

    @GetMapping
    public ResponseEntity<PaginationResponse<CardResponse>> getCards(
        @RequestHeader("SELECTED-USER") String requesterKey,
        @PathVariable String walletKey,
        @RequestParam(required = false) String cardKey,
        @RequestParam(required = false) String requestControlKey,
        @RequestParam(required = false) String documentNumber,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "30") int pageSize
    ) {
        PaginationResponse<CardResponse> cardResponses = cardService.getCards(requesterKey, walletKey, cardKey, requestControlKey, documentNumber, page, pageSize);
        return ResponseEntity.ok(cardResponses);
    }
}
