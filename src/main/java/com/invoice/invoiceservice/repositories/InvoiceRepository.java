package com.invoice.invoiceservice.repositories;

import com.invoice.invoiceservice.entities.Invoice;
import com.invoice.invoiceservice.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByWalletAndClosingDate(Wallet wallet, LocalDate closingDate);
}
