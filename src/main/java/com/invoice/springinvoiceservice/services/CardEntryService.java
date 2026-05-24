package com.invoice.springinvoiceservice.services;

import com.invoice.springinvoiceservice.commons.DateHandler;
import com.invoice.springinvoiceservice.commons.InstallmentHandler;
import com.invoice.springinvoiceservice.connectors.SnsConnector;
import com.invoice.springinvoiceservice.dtos.messages.CardEntryConclusionMessage;
import com.invoice.springinvoiceservice.dtos.messages.CardEntryMessage;
import com.invoice.springinvoiceservice.dtos.requests.CreateCardEntryRequest;
import com.invoice.springinvoiceservice.dtos.responses.CardEntryResponse;
import com.invoice.springinvoiceservice.entities.*;
import com.invoice.springinvoiceservice.exceptions.customexceptions.*;
import com.invoice.springinvoiceservice.repositories.*;
import jakarta.transaction.Transactional;
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
        snsConnector.publishMessage(CardEntryMessage.TOPIC_NAME, cardEntryConclusionMessage);
    }

    private LocalDate getInvoiceDueDate(InvoiceConfiguration invoiceConfiguration, LocalDate closingDate) {
        if (invoiceConfiguration.getDueType().getEnumerator().equals(DueTypeEnum.FIXED_DAY.name())) {
            YearMonth dueYearMonth = YearMonth.from(closingDate).plusMonths(invoiceConfiguration.getDueOffsetMonths());
            return DateHandler.createDateFromYearMonthDay(dueYearMonth.getYear(), dueYearMonth.getMonthValue(), invoiceConfiguration.getDueFixedDay());
        }

        return closingDate.plusDays(invoiceConfiguration.getDueDaysAfterClosing());
    }

    private List<Invoice> createWalletInvoices(Wallet wallet, CardEntry cardEntry, LocalDate currentDate) {
        List<Invoice> invoices = new ArrayList<>();

        YearMonth closingYearMonth = YearMonth.of(currentDate.getYear(), currentDate.getMonthValue());

        LocalDate closingDate = DateHandler.createDateFromYearMonthDay(
            closingYearMonth.getYear(),
            closingYearMonth.getMonthValue(),
            wallet.getInvoiceConfiguration().getClosingFixedDay()
        );

        if (currentDate.isAfter(closingDate)) {
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
                    closingYearMonth = closingYearMonth.plusMonths(1);
                    continue;
                }

                invoices.add(invoice);
            } else {
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
        }

        WalletLimit walletLimit = walletLimitRepository.findByWallet(wallet);

        BigDecimal walletLimitAmountAvailable = walletLimit.getLimitAmount().subtract(walletLimit.getUsedLimitAmount());
        if (createCardEntryRequest.getAmount().compareTo(walletLimitAmountAvailable) > 0) {
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

        return CardEntryResponse.from(
            cardEntry.getCardEntryKey(),
            cardEntry.getRequestControlKey(),
            cardEntry.getAmount(),
            cardEntry.getCardEntryType().getEnumerator(),
            cardEntry.getCardEntryStatus().getEnumerator()
        );
    }

    public void processCardEntry(CardEntryConclusionMessage cardEntryConclusionMessage) {
        LocalDate currentDate = DateHandler.getCurrentDateWithTimezone();

        Wallet wallet = walletRepository.findByWalletKeyForUpdate(cardEntryConclusionMessage.getWalletKey())
            .orElseThrow(WalletNotFoundException::new);

        CardEntry cardEntry = cardEntryRepository.findByCardEntryKey(cardEntryConclusionMessage.getCardEntryKey())
            .orElseThrow(CardEntryNotFoundException::new);

        if (!cardEntry.getCardEntryStatus().getEnumerator().equals(CardEntryStatusEnum.PROCESSING_CONCLUSION.name())) {
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
    }
}
