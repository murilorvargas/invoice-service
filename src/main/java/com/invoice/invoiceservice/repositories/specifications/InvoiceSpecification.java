package com.invoice.invoiceservice.repositories.specifications;

import com.invoice.invoiceservice.entities.Invoice;
import com.invoice.invoiceservice.entities.Wallet;
import org.springframework.data.jpa.domain.Specification;

public class InvoiceSpecification {

    private InvoiceSpecification() {}

    public static Specification<Invoice> withWallet(Wallet wallet) {
        return (root, query, cb) -> cb.equal(root.get("wallet"), wallet);
    }

    public static Specification<Invoice> withInvoiceKeyIfPresent(String invoiceKey) {
        return (root, query, cb) -> invoiceKey == null ? null : cb.equal(root.get("invoiceKey"), invoiceKey);
    }

    public static Specification<Invoice> withInvoiceStatusIfPresent(String invoiceStatus) {
        return (root, query, cb) -> invoiceStatus == null ? null : cb.equal(root.get("invoiceStatus").get("enumerator"), invoiceStatus);
    }
}
