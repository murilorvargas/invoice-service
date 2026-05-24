package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.Invoice;
import com.invoice.springinvoiceservice.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByWalletAndClosingDate(Wallet wallet, LocalDate closingDate);
}
