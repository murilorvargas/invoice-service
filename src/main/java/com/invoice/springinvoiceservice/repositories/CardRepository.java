package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardKey(String cardKey);
}
