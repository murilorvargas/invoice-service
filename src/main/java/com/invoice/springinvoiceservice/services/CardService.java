package com.invoice.springinvoiceservice.services;

import com.invoice.springinvoiceservice.dtos.requests.CreateCardRequest;
import com.invoice.springinvoiceservice.dtos.responses.CardResponse;
import com.invoice.springinvoiceservice.entities.Card;
import com.invoice.springinvoiceservice.entities.CardStatus;
import com.invoice.springinvoiceservice.entities.CardStatusEnum;
import com.invoice.springinvoiceservice.entities.Wallet;
import com.invoice.springinvoiceservice.exceptions.customexceptions.WalletNotFoundException;
import com.invoice.springinvoiceservice.repositories.CardRepository;
import com.invoice.springinvoiceservice.repositories.CardStatusRepository;
import com.invoice.springinvoiceservice.repositories.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

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
        Wallet wallet = walletRepository.findByWalletKey(walletKey)
            .orElseThrow(WalletNotFoundException::new);

        if (!requesterKey.equals(wallet.getRequesterKey())) {
            throw new WalletNotFoundException();
        }

        String cardStatusEnumerator = CardStatusEnum.ACTIVE.name();
        CardStatus cardStatus = cardStatusRepository.findByEnumerator(cardStatusEnumerator);

        String documentNumber = wallet.getDocumentNumber();
        if (createCardRequest.getOwner() != null) {
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
        return CardResponse.from(card);
    }
}
