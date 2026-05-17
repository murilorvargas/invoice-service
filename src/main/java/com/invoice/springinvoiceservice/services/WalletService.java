package com.invoice.springinvoiceservice.services;

import com.invoice.springinvoiceservice.entities.*;
import com.invoice.springinvoiceservice.repositories.*;
import com.invoice.springinvoiceservice.dtos.requests.CreateWalletRequest;
import com.invoice.springinvoiceservice.dtos.responses.WalletResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class WalletService {

    private final DueTypeRepository dueTypeRepository;
    private final InvoiceConfigurationRepository invoiceConfigurationRepository;
    private final WalletStatusRepository walletStatusRepository;
    private final WalletRepository walletRepository;
    private final WalletLimitRepository walletLimitRepository;

    public WalletService(
        DueTypeRepository dueTypeRepository,
        InvoiceConfigurationRepository invoiceConfigurationRepository,
        WalletStatusRepository walletStatusRepository,
        WalletRepository walletRepository,
        WalletLimitRepository walletLimitRepository
    ) {
        this.dueTypeRepository = dueTypeRepository;
        this.invoiceConfigurationRepository = invoiceConfigurationRepository;
        this.walletStatusRepository = walletStatusRepository;
        this.walletRepository = walletRepository;
        this.walletLimitRepository = walletLimitRepository;
    }

    public WalletResponse createWallet(String requesterKey, CreateWalletRequest createWalletRequest) {
        String dueTypeEnumerator = createWalletRequest.getInvoiceConfiguration().getDueType().name();
        DueType dueType = dueTypeRepository.findByEnumerator(dueTypeEnumerator);

        InvoiceConfiguration invoiceConfiguration = new InvoiceConfiguration(
            UUID.randomUUID().toString(),
            createWalletRequest.getInvoiceConfiguration().getClosingFixedDay(),
            createWalletRequest.getInvoiceConfiguration().getDueFixedDay(),
            createWalletRequest.getInvoiceConfiguration().getDueOffsetMonths(),
            createWalletRequest.getInvoiceConfiguration().getDueDaysAfterClosing(),
            new BigDecimal(createWalletRequest.getInvoiceConfiguration().getFinePercentage().toString()),
            new BigDecimal(createWalletRequest.getInvoiceConfiguration().getInterestPercentage().toString()),
            new BigDecimal(createWalletRequest.getInvoiceConfiguration().getRevolvingInterestPercentage().toString()),
            dueType
        );
        invoiceConfigurationRepository.save(invoiceConfiguration);

        String walletStatusEnumerator = WalletStatusEnum.ACTIVE.name();
        WalletStatus walletStatus = walletStatusRepository.findByEnumerator(walletStatusEnumerator);

        Wallet wallet = new Wallet(
            UUID.randomUUID().toString(),
            requesterKey,
            createWalletRequest.getRequestControlKey(),
            createWalletRequest.getOwner().getDocumentNumber(),
            invoiceConfiguration,
            walletStatus
        );
        walletRepository.save(wallet);

        List<WalletLimit> walletLimits = new ArrayList<>();

        WalletLimit walletLimit = new WalletLimit(
            UUID.randomUUID().toString(),
            createWalletRequest.getWalletLimit().getLimitAmount(),
            new BigDecimal("0"),
            wallet
        );
        walletLimitRepository.save(walletLimit);
        walletLimits.add(walletLimit);

        return WalletResponse.from(wallet, walletLimits);
    }
}
