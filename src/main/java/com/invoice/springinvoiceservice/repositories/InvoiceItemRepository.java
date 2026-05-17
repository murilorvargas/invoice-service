package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
}
