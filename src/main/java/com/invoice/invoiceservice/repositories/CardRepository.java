package com.invoice.invoiceservice.repositories;

import com.invoice.invoiceservice.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardKey(String cardKey);
}
