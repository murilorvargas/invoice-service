package com.invoice.invoiceservice.services;

import com.invoice.invoiceservice.dtos.responses.InvoiceGetResponse;
import com.invoice.invoiceservice.dtos.responses.commons.PaginationResponse;
import com.invoice.invoiceservice.entities.Invoice;
import com.invoice.invoiceservice.entities.Wallet;
import com.invoice.invoiceservice.exceptions.customexceptions.WalletNotFoundException;
import com.invoice.invoiceservice.repositories.InvoiceItemRepository;
import com.invoice.invoiceservice.repositories.InvoiceRepository;
import com.invoice.invoiceservice.repositories.WalletRepository;
import com.invoice.invoiceservice.repositories.specifications.InvoiceSpecification;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
public class InvoiceService {

    private final WalletRepository walletRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;

    public InvoiceService(
        WalletRepository walletRepository,
        InvoiceRepository invoiceRepository,
        InvoiceItemRepository invoiceItemRepository
    ) {
        this.walletRepository = walletRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceItemRepository = invoiceItemRepository;
    }

    public PaginationResponse<InvoiceGetResponse> getInvoices(
        String requesterKey,
        String walletKey,
        String invoiceKey,
        String invoiceStatus,
        int page,
        int pageSize
    ) {
        log.info("InvoiceService.getInvoices - start - walletKey: {}", walletKey);

        Wallet wallet = walletRepository.findByWalletKey(walletKey)
            .orElseThrow(WalletNotFoundException::new);

        if (!requesterKey.equals(wallet.getRequesterKey())) {
            log.info("InvoiceService.getInvoices - requester does not own wallet {}", walletKey);
            throw new WalletNotFoundException();
        }

        Specification<Invoice> spec = Specification.where(InvoiceSpecification.withWallet(wallet))
            .and(InvoiceSpecification.withInvoiceKeyIfPresent(invoiceKey))
            .and(InvoiceSpecification.withInvoiceStatusIfPresent(invoiceStatus));

        List<InvoiceGetResponse> invoices = invoiceRepository.findAll(spec, PageRequest.of(page - 1, pageSize))
            .getContent()
            .stream()
            .map(invoice -> InvoiceGetResponse.from(invoice, invoiceItemRepository.findAllByInvoice(invoice)))
            .toList();

        log.info("InvoiceService.getInvoices - finished - walletKey: {}, total: {}", walletKey, invoices.size());
        return PaginationResponse.of(invoices, page, pageSize);
    }
}
