package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
