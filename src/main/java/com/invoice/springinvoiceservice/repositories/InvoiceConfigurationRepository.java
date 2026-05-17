package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.InvoiceConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceConfigurationRepository extends JpaRepository<InvoiceConfiguration, Long> {
}
