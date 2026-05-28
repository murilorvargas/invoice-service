package com.invoice.invoiceservice.repositories;

import com.invoice.invoiceservice.entities.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceStatusRepository extends JpaRepository<InvoiceStatus, Long> {

    InvoiceStatus findByEnumerator(String enumerator);
}
