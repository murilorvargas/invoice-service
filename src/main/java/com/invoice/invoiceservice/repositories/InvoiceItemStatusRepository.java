package com.invoice.invoiceservice.repositories;

import com.invoice.invoiceservice.entities.InvoiceItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceItemStatusRepository extends JpaRepository<InvoiceItemStatus, Long> {
    InvoiceItemStatus findByEnumerator(String enumerator);
}
