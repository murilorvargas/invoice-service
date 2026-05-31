package com.invoice.invoiceservice.controllers;

import com.invoice.invoiceservice.dtos.responses.InvoiceGetResponse;
import com.invoice.invoiceservice.dtos.responses.commons.PaginationResponse;
import com.invoice.invoiceservice.services.InvoiceService;
import com.invoice.invoiceservice.dtos.requests.commons.InvoiceStatusRequest;
import jakarta.validation.constraints.Max;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/wallets/{walletKey}/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<PaginationResponse<InvoiceGetResponse>> getInvoices(
        @RequestHeader("SELECTED-USER") String requesterKey,
        @PathVariable String walletKey,
        @RequestParam(required = false) String invoiceKey,
        @RequestParam(required = false) InvoiceStatusRequest invoiceStatus,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "30") @Max(100) int pageSize
    ) {
        PaginationResponse<InvoiceGetResponse> invoiceResponses = invoiceService.getInvoices(requesterKey, walletKey, invoiceKey, invoiceStatus != null ? invoiceStatus.name() : null, page, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(invoiceResponses);
    }
}
