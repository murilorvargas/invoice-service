package com.invoice.springinvoiceservice.services;

import com.invoice.springinvoiceservice.dtos.requests.CreateCardEntryRequest;
import com.invoice.springinvoiceservice.dtos.responses.CardEntryResponse;
import com.invoice.springinvoiceservice.repositories.CardRepository;
import com.invoice.springinvoiceservice.repositories.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CardEntryService {

    private final WalletRepository walletRepository;
    private final CardRepository cardRepository;

    public CardEntryService(

    ) {

    }

    public CardEntryResponse createCardEntry(
        String requesterKey,
        String walletKey,
        String cardKey,
        CreateCardEntryRequest createCardEntryRequest
    ) {

        return null;
    }
}
