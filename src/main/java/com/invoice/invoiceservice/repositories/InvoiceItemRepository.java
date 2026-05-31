package com.invoice.invoiceservice.repositories;

import com.invoice.invoiceservice.entities.Invoice;
import com.invoice.invoiceservice.entities.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

    List<InvoiceItem> findAllByInvoice(Invoice invoice);
}
