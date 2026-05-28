package com.invoice.invoiceservice.services;

import com.invoice.invoiceservice.commons.DateHandler;
import com.invoice.invoiceservice.commons.InstallmentHandler;
import com.invoice.invoiceservice.connectors.SnsConnector;
import com.invoice.invoiceservice.dtos.messages.CardEntryConclusionMessage;
import com.invoice.invoiceservice.dtos.messages.CardEntryMessage;
import com.invoice.invoiceservice.dtos.requests.CreateCardEntryRequest;
import com.invoice.invoiceservice.dtos.responses.CardEntryResponse;
import com.invoice.invoiceservice.entities.*;
import com.invoice.invoiceservice.exceptions.customexceptions.*;
import com.invoice.invoiceservice.repositories.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class CardEntryService {

    private final SnsConnector snsConnector;

    private final WalletRepository walletRepository;
    private final CardRepository cardRepository;
    private final WalletLimitRepository walletLimitRepository;
    private final CardEntryStatusRepository cardEntryStatusRepository;
    private final CardEntryTypeRepository cardEntryTypeRepository;
    private final CardEntryRepository cardEntryRepository;
    private final InvoiceStatusRepository invoiceStatusRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemStatusRepository invoiceItemStatusRepository;
    private final InvoiceItemRepository invoiceItemRepository;

    public CardEntryService(
        SnsConnector snsConnector,
        WalletRepository walletRepository,
        CardRepository cardRepository,
        WalletLimitRepository walletLimitRepository,
        CardEntryStatusRepository cardEntryStatusRepository,
        CardEntryTypeRepository cardEntryTypeRepository,
        CardEntryRepository cardEntryRepository,
        InvoiceStatusRepository invoiceStatusRepository,
        InvoiceRepository invoiceRepository,
        InvoiceItemStatusRepository invoiceItemStatusRepository,
        InvoiceItemRepository invoiceItemRepository
    ) {
        this.snsConnector = snsConnector;
        this.walletRepository = walletRepository;
        this.cardRepository = cardRepository;
        this.walletLimitRepository = walletLimitRepository;
        this.cardEntryStatusRepository = cardEntryStatusRepository;
        this.cardEntryTypeRepository = cardEntryTypeRepository;
        this.cardEntryRepository = cardEntryRepository;
        this.invoiceStatusRepository = invoiceStatusRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceItemStatusRepository = invoiceItemStatusRepository;
        this.invoiceItemRepository = invoiceItemRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    private void publishCardEntryConclusionMessage(CardEntryConclusionMessage cardEntryConclusionMessage) {
        log.info("CardEntryService.publishCardEntryConclusionMessage - start - walletKey: {}, cardEntryKey: {}",
            cardEntryConclusionMessage.getWalletKey(), cardEntryConclusionMessage.getCardEntryKey());
        snsConnector.publishMessage(CardEntryMessage.TOPIC_NAME, cardEntryConclusionMessage);
    }

    private LocalDate getInvoiceDueDate(InvoiceConfiguration invoiceConfiguration, LocalDate closingDate) {
        log.info("CardEntryService.getInvoiceDueDate - start - dueType: {}, closingDate: {}",
            invoiceConfiguration.getDueType().getEnumerator(), closingDate);

        if (invoiceConfiguration.getDueType().getEnumerator().equals(DueTypeEnum.FIXED_DAY.name())) {
            log.info("CardEntryService.getInvoiceDueDate - using FIXED_DAY strategy");
            YearMonth dueYearMonth = YearMonth.from(closingDate).plusMonths(invoiceConfiguration.getDueOffsetMonths());
            return DateHandler.createDateFromYearMonthDay(dueYearMonth.getYear(), dueYearMonth.getMonthValue(), invoiceConfiguration.getDueFixedDay());
        }

        log.info("CardEntryService.getInvoiceDueDate - using RULE strategy (days after closing)");
        return closingDate.plusDays(invoiceConfiguration.getDueDaysAfterClosing());
    }

    private List<Invoice> createWalletInvoices(Wallet wallet, CardEntry cardEntry, LocalDate currentDate) {
        log.info("CardEntryService.createWalletInvoices - start - walletKey: {}, numberOfInstallments: {}",
            wallet.getWalletKey(), cardEntry.getNumberOfInstallments());

        List<Invoice> invoices = new ArrayList<>();

        YearMonth closingYearMonth = YearMonth.of(currentDate.getYear(), currentDate.getMonthValue());

        LocalDate closingDate = DateHandler.createDateFromYearMonthDay(
            closingYearMonth.getYear(),
            closingYearMonth.getMonthValue(),
            wallet.getInvoiceConfiguration().getClosingFixedDay()
        );

        if (currentDate.isAfter(closingDate)) {
            log.info("CardEntryService.createWalletInvoices - current date is after closing date, shifting to next month");
            closingYearMonth = closingYearMonth.plusMonths(1);
        }

        while (invoices.size() < cardEntry.getNumberOfInstallments()) {
            closingDate = DateHandler.createDateFromYearMonthDay(
                closingYearMonth.getYear(),
                closingYearMonth.getMonthValue(),
                wallet.getInvoiceConfiguration().getClosingFixedDay()
            );

            Optional<Invoice> optionalInvoice = invoiceRepository.findByWalletAndClosingDate(wallet, closingDate);
            if (optionalInvoice.isPresent()) {
                Invoice invoice = optionalInvoice.get();
                if (!invoice.getInvoiceStatus().getEnumerator().equals(InvoiceStatusEnum.OPENED.name())) {
                    log.info("CardEntryService.createWalletInvoices - invoice for closing date {} is not open, skipping to next month", closingDate);
                    closingYearMonth = closingYearMonth.plusMonths(1);
                    continue;
                }

                log.info("CardEntryService.createWalletInvoices - reusing existing open invoice for closing date {}", closingDate);
                invoices.add(invoice);
            } else {
                log.info("CardEntryService.createWalletInvoices - creating new invoice for closing date {}", closingDate);
                LocalDate dueDate = getInvoiceDueDate(wallet.getInvoiceConfiguration(), closingDate);

                InvoiceStatus invoiceStatus = invoiceStatusRepository.findByEnumerator(InvoiceStatusEnum.OPENED.name());

                Invoice invoice = new Invoice(
                    UUID.randomUUID().toString(),
                    closingDate,
                    dueDate,
                    new BigDecimal("0"),
                    wallet,
                    invoiceStatus
                );
                invoiceRepository.save(invoice);

                invoices.add(invoice);
            }

            closingYearMonth = closingYearMonth.plusMonths(1);
        }

        return invoices;
    }

    public CardEntryResponse createCardEntry(
        String requesterKey,
        String walletKey,
        String cardKey,
        CreateCardEntryRequest createCardEntryRequest
    ) {
        log.info("CardEntryService.createCardEntry - start - walletKey: {}, cardKey: {}", walletKey, cardKey);

        Wallet wallet = walletRepository.findByWalletKey(walletKey)
            .orElseThrow(WalletNotFoundException::new);

        if (!requesterKey.equals(wallet.getRequesterKey())) {
            log.info("CardEntryService.createCardEntry - requester does not own wallet {}", walletKey);
            throw new WalletNotFoundException();
        }

        if (!wallet.getWalletStatus().getEnumerator().equals(WalletStatusEnum.ACTIVE.name())) {
            log.info("CardEntryService.createCardEntry - wallet {} is not active", walletKey);
            throw new WalletNotActiveException();
        }

        Card card = cardRepository.findByCardKey(cardKey)
            .orElseThrow(CardNotFoundException::new);

        if (!card.getWallet().getWalletKey().equals(walletKey)) {
            log.info("CardEntryService.createCardEntry - card {} does not belong to wallet {}", cardKey, walletKey);
            throw new CardNotFoundException();
        }

        if (!card.getCardStatus().getEnumerator().equals(CardStatusEnum.ACTIVE.name())) {
            log.info("CardEntryService.createCardEntry - card {} is not active", cardKey);
            throw new CardNotActiveException();
        }

        if (card.getMonthlyLimitAmount() != null) {
            log.info("CardEntryService.createCardEntry - card has monthly limit, checking availability");
            BigDecimal cardMonthlyLimitAmountAvailable = card.getMonthlyLimitAmount().subtract(card.getUsedMonthlyLimitAmount());
            if (createCardEntryRequest.getAmount().compareTo(cardMonthlyLimitAmountAvailable) > 0) {
                log.info("CardEntryService.createCardEntry - card monthly limit exceeded for card {}", cardKey);
                throw new CardMonthlyLimitExceededException();
            }

            card.setUsedMonthlyLimitAmount(card.getUsedMonthlyLimitAmount().add(createCardEntryRequest.getAmount()));
        }

        WalletLimit walletLimit = walletLimitRepository.findByWallet(wallet);

        BigDecimal walletLimitAmountAvailable = walletLimit.getLimitAmount().subtract(walletLimit.getUsedLimitAmount());
        if (createCardEntryRequest.getAmount().compareTo(walletLimitAmountAvailable) > 0) {
            log.info("CardEntryService.createCardEntry - wallet limit exceeded for wallet {}", walletKey);
            throw new WalletLimitExceededException();
        }

        walletLimit.setUsedLimitAmount(walletLimit.getUsedLimitAmount().add(createCardEntryRequest.getAmount()));

        CardEntryData cardEntryData = new CardEntryData(
            createCardEntryRequest.getCardEntryData().getMerchantName()
        );

        CardEntryStatus cardEntryStatus = cardEntryStatusRepository.findByEnumerator(CardEntryStatusEnum.PROCESSING_CONCLUSION.name());
        CardEntryType cardEntryType = cardEntryTypeRepository.findByEnumerator(createCardEntryRequest.getCardEntryType().name());

        CardEntry cardEntry = new CardEntry(
            UUID.randomUUID().toString(),
            createCardEntryRequest.getRequestControlKey(),
            createCardEntryRequest.getAmount(),
            createCardEntryRequest.getNumberOfInstallments(),
            cardEntryData,
            card,
            cardEntryStatus,
            cardEntryType
        );

        cardEntryRepository.save(cardEntry);

        publishCardEntryConclusionMessage(new CardEntryConclusionMessage(wallet.getWalletKey(), cardEntry.getCardEntryKey()));

        log.info("CardEntryService.createCardEntry - finished - cardEntryKey: {}", cardEntry.getCardEntryKey());
        return CardEntryResponse.from(
            cardEntry.getCardEntryKey(),
            cardEntry.getRequestControlKey(),
            cardEntry.getAmount(),
            cardEntry.getCardEntryType().getEnumerator(),
            cardEntry.getCardEntryStatus().getEnumerator()
        );
    }

    public void processCardEntryConclusion(CardEntryConclusionMessage cardEntryConclusionMessage) {
        log.info("CardEntryService.processCardEntryConclusion - start - walletKey: {}, cardEntryKey: {}",
            cardEntryConclusionMessage.getWalletKey(), cardEntryConclusionMessage.getCardEntryKey());

        LocalDate currentDate = DateHandler.getCurrentDateWithTimezone();

        Wallet wallet = walletRepository.findByWalletKeyForUpdate(cardEntryConclusionMessage.getWalletKey())
            .orElseThrow(WalletNotFoundException::new);

        CardEntry cardEntry = cardEntryRepository.findByCardEntryKey(cardEntryConclusionMessage.getCardEntryKey())
            .orElseThrow(CardEntryNotFoundException::new);

        if (!cardEntry.getCardEntryStatus().getEnumerator().equals(CardEntryStatusEnum.PROCESSING_CONCLUSION.name())) {
            log.info("CardEntryService.processCardEntryConclusion - card entry {} is not in PROCESSING_CONCLUSION status",
                cardEntryConclusionMessage.getCardEntryKey());
            throw new CardEntryNotInProcessingConclusionStatusException();
        }

        List<Invoice> invoices = createWalletInvoices(wallet, cardEntry, currentDate);
        List<BigDecimal> installmentAmounts = InstallmentHandler.calculateInstallmentAmounts(cardEntry.getAmount(), cardEntry.getNumberOfInstallments());

        InvoiceItemStatus invoiceItemStatus = invoiceItemStatusRepository.findByEnumerator(InvoiceItemStatusEnum.CONCLUDED.name());

        for (int i = 0; i < cardEntry.getNumberOfInstallments(); i++) {
            String invoiceDescription = cardEntry.getNumberOfInstallments() > 1
                ? String.format("%s - %d/%d", cardEntry.getCardEntryData().merchantName().strip(), i + 1, cardEntry.getNumberOfInstallments())
                : cardEntry.getCardEntryData().merchantName().strip();

            Invoice invoice = invoices.get(i);
            invoice.setAmount(invoice.getAmount().add(installmentAmounts.get(i)));

            InvoiceItem invoiceItem = new InvoiceItem(
                UUID.randomUUID().toString(),
                invoiceDescription,
                installmentAmounts.get(i),
                invoices.get(i),
                invoiceItemStatus
            );
            invoiceItemRepository.save(invoiceItem);
        }

        cardEntry.setCardEntryStatus(cardEntryStatusRepository.findByEnumerator(CardEntryStatusEnum.CONCLUDED.name()));

        log.info("CardEntryService.processCardEntryConclusion - finished - cardEntryKey: {}",
            cardEntryConclusionMessage.getCardEntryKey());
    }
}
