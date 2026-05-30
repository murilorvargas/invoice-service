package com.invoice.invoiceservice.services;

import com.invoice.invoiceservice.entities.*;
import com.invoice.invoiceservice.repositories.*;
import com.invoice.invoiceservice.dtos.requests.CreateWalletRequest;
import com.invoice.invoiceservice.exceptions.customexceptions.WalletNotFoundException;
import com.invoice.invoiceservice.dtos.responses.commons.PaginationResponse;
import com.invoice.invoiceservice.dtos.responses.WalletCreateResponse;
import com.invoice.invoiceservice.dtos.responses.WalletGetByKeyResponse;
import com.invoice.invoiceservice.dtos.responses.WalletGetResponse;
import com.invoice.invoiceservice.repositories.specifications.WalletSpecification;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
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

    public WalletCreateResponse createWallet(String requesterKey, CreateWalletRequest createWalletRequest) {
        log.info("WalletService.createWallet - start - requesterKey: {}", requesterKey);
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

        log.info("WalletService.createWallet - finished - walletKey: {}", wallet.getWalletKey());
        return WalletCreateResponse.from(wallet, walletLimits);
    }

    public WalletGetByKeyResponse getWalletByKey(String requesterKey, String walletKey) {
        log.info("WalletService.getWalletByKey - start - walletKey: {}", walletKey);

        Wallet wallet = walletRepository.findByWalletKey(walletKey)
            .orElseThrow(WalletNotFoundException::new);

        if (!requesterKey.equals(wallet.getRequesterKey())) {
            log.info("WalletService.getWalletByKey - requester does not own wallet {}", walletKey);
            throw new WalletNotFoundException();
        }

        List<WalletLimit> walletLimits = walletLimitRepository.findAllByWallet(wallet);

        log.info("WalletService.getWalletByKey - finished - walletKey: {}", walletKey);
        return WalletGetByKeyResponse.from(wallet, walletLimits);
    }

    public PaginationResponse<WalletGetResponse> getWallets(
        String requesterKey,
        String walletKey,
        String requestControlKey,
        String documentNumber,
        int page,
        int pageSize
    ) {
        log.info("WalletService.getWallets - start - requesterKey: {}", requesterKey);

        Specification<Wallet> spec = Specification.where(WalletSpecification.withRequesterKey(requesterKey))
            .and(WalletSpecification.withWalletKeyIfPresent(walletKey))
            .and(WalletSpecification.withRequestControlKeyIfPresent(requestControlKey))
            .and(WalletSpecification.withDocumentNumberIfPresent(documentNumber));

        List<WalletGetResponse> wallets = walletRepository.findAll(spec, PageRequest.of(page - 1, pageSize))
            .getContent()
            .stream()
            .map(WalletGetResponse::from)
            .toList();

        log.info("WalletService.getWallets - finished - requesterKey: {}, total: {}", requesterKey, wallets.size());
        return PaginationResponse.of(wallets, page, pageSize);
    }
}
