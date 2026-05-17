package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
}
