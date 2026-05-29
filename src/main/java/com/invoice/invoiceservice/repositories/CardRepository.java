package com.invoice.invoiceservice.repositories;

import com.invoice.invoiceservice.entities.Card;
import com.invoice.invoiceservice.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {

    Optional<Card> findByWallet(Wallet wallet);

    Optional<Card> findByCardKey(String cardKey);
}
