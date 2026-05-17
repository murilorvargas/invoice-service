package com.invoice.springinvoiceservice.services;

import com.invoice.springinvoiceservice.dtos.messages.CardEntryConclusionMessage;
import com.invoice.springinvoiceservice.dtos.requests.CreateCardEntryRequest;
import com.invoice.springinvoiceservice.dtos.responses.CardEntryResponse;
import com.invoice.springinvoiceservice.entities.*;
import com.invoice.springinvoiceservice.exceptions.customexceptions.*;
import com.invoice.springinvoiceservice.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
public class CardEntryService {

    private final WalletRepository walletRepository;
    private final CardRepository cardRepository;
    private final WalletLimitRepository walletLimitRepository;
    private final CardEntryStatusRepository cardEntryStatusRepository;
    private final CardEntryTypeRepository cardEntryTypeRepository;
    private final CardEntryRepository cardEntryRepository;

    public CardEntryService(
        WalletRepository walletRepository,
        CardRepository cardRepository,
        WalletLimitRepository walletLimitRepository,
        CardEntryStatusRepository cardEntryStatusRepository,
        CardEntryTypeRepository cardEntryTypeRepository,
        CardEntryRepository cardEntryRepository
    ) {
        this.walletRepository = walletRepository;
        this.cardRepository = cardRepository;
        this.walletLimitRepository = walletLimitRepository;
        this.cardEntryStatusRepository = cardEntryStatusRepository;
        this.cardEntryTypeRepository = cardEntryTypeRepository;
        this.cardEntryRepository = cardEntryRepository;
    }

    public CardEntryResponse createCardEntry(
        String requesterKey,
        String walletKey,
        String cardKey,
        CreateCardEntryRequest createCardEntryRequest
    ) {
        Wallet wallet = walletRepository.findByWalletKey(walletKey)
            .orElseThrow(WalletNotFoundException::new);

        if (!requesterKey.equals(wallet.getRequesterKey())) {
            throw new WalletNotFoundException();
        }

        if (!wallet.getWalletStatus().getEnumerator().equals(WalletStatusEnum.ACTIVE.name())) {
            throw new WalletNotActiveException();
        }

        Card card = cardRepository.findByCardKey(cardKey)
            .orElseThrow(CardNotFoundException::new);

        if (!card.getWallet().getWalletKey().equals(walletKey)) {
            throw new CardNotFoundException();
        }

        if (!card.getCardStatus().getEnumerator().equals(CardStatusEnum.ACTIVE.name())) {
            throw new CardNotActiveException();
        }

        if (card.getMonthlyLimitAmount() != null) {
            BigDecimal cardMonthlyLimitAmountAvailable = card.getMonthlyLimitAmount().subtract(card.getUsedMonthlyLimitAmount());
            if (createCardEntryRequest.getAmount().compareTo(cardMonthlyLimitAmountAvailable) > 0) {
                throw new CardMonthlyLimitExceededException();
            }

            card.setUsedMonthlyLimitAmount(card.getUsedMonthlyLimitAmount().add(createCardEntryRequest.getAmount()));
            cardRepository.save(card);
        }

        WalletLimit walletLimit = walletLimitRepository.findByWallet(wallet);

        BigDecimal walletLimitAmountAvailable = walletLimit.getLimitAmount().subtract(walletLimit.getUsedLimitAmount());
        if (createCardEntryRequest.getAmount().compareTo(walletLimitAmountAvailable) > 0) {
            throw new WalletLimitExceededException();
        }

        walletLimit.setUsedLimitAmount(walletLimit.getUsedLimitAmount().add(createCardEntryRequest.getAmount()));
        walletLimitRepository.save(walletLimit);

        CardEntryStatus cardEntryStatus = cardEntryStatusRepository.findByEnumerator(CardEntryStatusEnum.PROCESSING_CONCLUSION.name());
        CardEntryType cardEntryType = cardEntryTypeRepository.findByEnumerator(createCardEntryRequest.getCardEntryType().name());

        CardEntry cardEntry = new CardEntry(
            UUID.randomUUID().toString(),
            createCardEntryRequest.getRequestControlKey(),
            createCardEntryRequest.getAmount(),
            card,
            cardEntryStatus,
            cardEntryType
        );

        cardEntryRepository.save(cardEntry);

        // PUBLISH MESSAGE TO CREATE INVOICE ITEMS AND INVOICES IF NECESSARY

        return CardEntryResponse.from(
            cardEntry.getCardEntryKey(),
            cardEntry.getRequestControlKey(),
            cardEntry.getAmount(),
            cardEntry.getCardEntryType().getEnumerator(),
            cardEntry.getCardEntryStatus().getEnumerator()
        );
    }

    public void processCardEntry(CardEntryConclusionMessage cardEntryConclusionMessage) {

    }
}
