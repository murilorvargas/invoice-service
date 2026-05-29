package com.invoice.invoiceservice.repositories.specifications;

import com.invoice.invoiceservice.entities.Card;
import com.invoice.invoiceservice.entities.Wallet;
import org.springframework.data.jpa.domain.Specification;

public class CardSpecification {

    private CardSpecification() {}

    public static Specification<Card> withWallet(Wallet wallet) {
        return (root, query, cb) -> cb.equal(root.get("wallet"), wallet);
    }

    public static Specification<Card> withCardKeyIfPresent(String cardKey) {
        return (root, query, cb) -> cardKey == null ? null : cb.equal(root.get("cardKey"), cardKey);
    }

    public static Specification<Card> withRequestControlKeyIfPresent(String requestControlKey) {
        return (root, query, cb) -> requestControlKey == null ? null : cb.equal(root.get("requestControlKey"), requestControlKey);
    }

    public static Specification<Card> withDocumentNumberIfPresent(String documentNumber) {
        return (root, query, cb) -> documentNumber == null ? null : cb.equal(root.get("documentNumber"), documentNumber);
    }
}
