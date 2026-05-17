package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
