package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.InvoiceItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceItemStatusRepository extends JpaRepository<InvoiceItemStatus, Long> {
    InvoiceItemStatus findByEnumerator(String enumerator);
}
