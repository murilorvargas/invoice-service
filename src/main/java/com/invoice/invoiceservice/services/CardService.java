package com.invoice.invoiceservice.services;

import com.invoice.invoiceservice.dtos.requests.CreateCardRequest;
import com.invoice.invoiceservice.dtos.responses.CardCreateResponse;
import com.invoice.invoiceservice.dtos.responses.CardGetResponse;
import com.invoice.invoiceservice.dtos.responses.commons.PaginationResponse;
import com.invoice.invoiceservice.entities.*;
import com.invoice.invoiceservice.exceptions.customexceptions.WalletNotActiveException;
import com.invoice.invoiceservice.exceptions.customexceptions.WalletNotFoundException;
import com.invoice.invoiceservice.repositories.CardRepository;
import com.invoice.invoiceservice.repositories.CardStatusRepository;
import com.invoice.invoiceservice.repositories.WalletRepository;
import com.invoice.invoiceservice.repositories.specifications.CardSpecification;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
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

    public CardCreateResponse createCard(String requesterKey, String walletKey, CreateCardRequest createCardRequest) {
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

        BigDecimal usedMonthlyLimitAmount = createCardRequest.getMonthlyLimitAmount() != null ? new BigDecimal("0") : null;

        Card card = new Card(
            UUID.randomUUID().toString(),
            createCardRequest.getRequestControlKey(),
            documentNumber,
            createCardRequest.getMonthlyLimitAmount(),
            usedMonthlyLimitAmount,
            wallet,
            cardStatus
        );
        cardRepository.save(card);

        log.info("CardService.createCard - finished - cardKey: {}", card.getCardKey());
        return CardCreateResponse.from(card);
    }

    public PaginationResponse<CardGetResponse> getCards(
        String requesterKey,
        String walletKey,
        String cardKey,
        String requestControlKey,
        String documentNumber,
        int page,
        int pageSize
    ) {
        log.info("CardService.getCards - start - walletKey: {}", walletKey);

        Wallet wallet = walletRepository.findByWalletKey(walletKey)
            .orElseThrow(WalletNotFoundException::new);

        if (!requesterKey.equals(wallet.getRequesterKey())) {
            log.info("CardService.getCards - requester does not own wallet {}", walletKey);
            throw new WalletNotFoundException();
        }

        Specification<Card> spec = Specification.where(CardSpecification.withWallet(wallet))
            .and(CardSpecification.withCardKeyIfPresent(cardKey))
            .and(CardSpecification.withRequestControlKeyIfPresent(requestControlKey))
            .and(CardSpecification.withDocumentNumberIfPresent(documentNumber));

        List<CardGetResponse> cards = cardRepository.findAll(spec, PageRequest.of(page - 1, pageSize))
            .getContent()
            .stream()
            .map(CardGetResponse::from)
            .toList();

        log.info("CardService.getCards - finished - walletKey: {}, total: {}", walletKey, cards.size());
        return PaginationResponse.of(cards, page, pageSize);
    }
}
