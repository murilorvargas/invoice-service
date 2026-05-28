package com.invoice.invoiceservice.repositories;

import com.invoice.invoiceservice.entities.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
}
