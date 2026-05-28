package com.invoice.invoiceservice.services;

import com.invoice.invoiceservice.dtos.requests.CreateCardRequest;
import com.invoice.invoiceservice.dtos.responses.CardResponse;
import com.invoice.invoiceservice.entities.*;
import com.invoice.invoiceservice.exceptions.customexceptions.WalletNotActiveException;
import com.invoice.invoiceservice.exceptions.customexceptions.WalletNotFoundException;
import com.invoice.invoiceservice.repositories.CardRepository;
import com.invoice.invoiceservice.repositories.CardStatusRepository;
import com.invoice.invoiceservice.repositories.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class CardService {

    private final WalletRepository walletRepository;
    private final CardStatusRepository cardStatusRepository;
    private final CardRepository cardRepository;

    public CardService(
        WalletRepository walletRepository,
        CardStatusRepository cardStatusRepository,
        CardRepository cardRepository
    ) {
        this.walletRepository = walletRepository;
        this.cardStatusRepository = cardStatusRepository;
        this.cardRepository = cardRepository;
    }

    public CardResponse createCard(String requesterKey, String walletKey, CreateCardRequest createCardRequest) {
        log.info("CardService.createCard - start - walletKey: {}", walletKey);

        Wallet wallet = walletRepository.findByWalletKey(walletKey)
            .orElseThrow(WalletNotFoundException::new);

        if (!requesterKey.equals(wallet.getRequesterKey())) {
            log.info("CardService.createCard - requester does not own wallet {}", walletKey);
            throw new WalletNotFoundException();
        }

        if (!wallet.getWalletStatus().getEnumerator().equals(WalletStatusEnum.ACTIVE.name())) {
            log.info("CardService.createCard - wallet {} is not active", walletKey);
            throw new WalletNotActiveException();
        }

        String cardStatusEnumerator = CardStatusEnum.ACTIVE.name();
        CardStatus cardStatus = cardStatusRepository.findByEnumerator(cardStatusEnumerator);

        String documentNumber = wallet.getDocumentNumber();
        if (createCardRequest.getOwner() != null) {
            log.info("CardService.createCard - using owner document number instead of wallet owner");
            documentNumber = createCardRequest.getOwner().getDocumentNumber();
        }

        Card card = new Card(
            UUID.randomUUID().toString(),
            createCardRequest.getRequestControlKey(),
            documentNumber,
            createCardRequest.getMonthlyLimitAmount(),
            new BigDecimal("0"),
            wallet,
            cardStatus
        );
        cardRepository.save(card);

        log.info("CardService.createCard - finished - cardKey: {}", card.getCardKey());
        return CardResponse.from(card);
    }
}
