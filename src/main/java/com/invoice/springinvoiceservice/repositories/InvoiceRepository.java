package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
