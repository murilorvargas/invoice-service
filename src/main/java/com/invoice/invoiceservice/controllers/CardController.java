package com.invoice.invoiceservice.controllers;

import com.invoice.invoiceservice.dtos.requests.CreateCardRequest;
import com.invoice.invoiceservice.dtos.responses.CardCreateResponse;
import com.invoice.invoiceservice.dtos.responses.CardGetResponse;
import com.invoice.invoiceservice.dtos.responses.commons.PaginationResponse;
import com.invoice.invoiceservice.services.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/wallets/{walletKey}/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardCreateResponse> createCard(
        @RequestHeader("SELECTED-USER") String requesterKey,
        @PathVariable String walletKey,
        @Valid @RequestBody CreateCardRequest createCardRequest
    ) {
        CardCreateResponse createCardResponse = cardService.createCard(requesterKey, walletKey, createCardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createCardResponse);
    }

    @GetMapping
    public ResponseEntity<PaginationResponse<CardGetResponse>> getCards(
        @RequestHeader("SELECTED-USER") String requesterKey,
        @PathVariable String walletKey,
        @RequestParam(required = false) String cardKey,
        @RequestParam(required = false) String requestControlKey,
        @RequestParam(required = false) String documentNumber,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "30") @Max(100) int pageSize
    ) {
        PaginationResponse<CardGetResponse> cardResponses = cardService.getCards(requesterKey, walletKey, cardKey, requestControlKey, documentNumber, page, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(cardResponses);
    }
}
