package com.invoice.invoiceservice.repositories.specifications;

import com.invoice.invoiceservice.entities.Wallet;
import org.springframework.data.jpa.domain.Specification;

public class WalletSpecification {

    private WalletSpecification() {}

    public static Specification<Wallet> withRequesterKey(String requesterKey) {
        return (root, query, cb) -> cb.equal(root.get("requesterKey"), requesterKey);
    }

    public static Specification<Wallet> withWalletKeyIfPresent(String walletKey) {
        return (root, query, cb) -> walletKey == null ? null : cb.equal(root.get("walletKey"), walletKey);
    }

    public static Specification<Wallet> withRequestControlKeyIfPresent(String requestControlKey) {
        return (root, query, cb) -> requestControlKey == null ? null : cb.equal(root.get("requestControlKey"), requestControlKey);
    }

    public static Specification<Wallet> withDocumentNumberIfPresent(String documentNumber) {
        return (root, query, cb) -> documentNumber == null ? null : cb.equal(root.get("documentNumber"), documentNumber);
    }
}
