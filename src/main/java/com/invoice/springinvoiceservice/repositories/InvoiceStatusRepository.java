package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceStatusRepository extends JpaRepository<InvoiceStatus, Long> {

    InvoiceStatus findByEnumerator(String enumerator);
}
