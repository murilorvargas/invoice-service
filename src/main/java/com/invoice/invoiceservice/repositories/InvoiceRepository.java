package com.invoice.invoiceservice.repositories;

import com.invoice.invoiceservice.entities.Invoice;
import com.invoice.invoiceservice.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    Optional<Invoice> findByWalletAndClosingDate(Wallet wallet, LocalDate closingDate);
}
