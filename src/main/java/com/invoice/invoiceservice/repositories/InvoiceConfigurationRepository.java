package com.invoice.invoiceservice.repositories;

import com.invoice.invoiceservice.entities.InvoiceConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceConfigurationRepository extends JpaRepository<InvoiceConfiguration, Long> {
}
