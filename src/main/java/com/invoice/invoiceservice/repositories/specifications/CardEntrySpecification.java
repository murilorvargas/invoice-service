package com.invoice.invoiceservice.repositories.specifications;

import com.invoice.invoiceservice.entities.Card;
import com.invoice.invoiceservice.entities.CardEntry;
import org.springframework.data.jpa.domain.Specification;

public class CardEntrySpecification {

    private CardEntrySpecification () {}

    public static Specification<CardEntry> withCard(Card card) {
        return (root, query, cb) -> cb.equal(root.get("card"), card);
    }

    public static Specification<CardEntry> withCardEntryKeyIfPresent(String cardEntryKey) {
        return (root, query, cb) -> cardEntryKey == null ? null : cb.equal(root.get("cardEntryKey"), cardEntryKey);
    }

    public static Specification<CardEntry> withRequestControlKeyIfPresent(String requestControlKey) {
        return (root, query, cb) -> requestControlKey == null ? null : cb.equal(root.get("requestControlKey"), requestControlKey);
    }
}
