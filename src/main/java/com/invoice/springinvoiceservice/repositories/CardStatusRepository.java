package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardStatusRepository extends JpaRepository<CardStatus, Long> {

    CardStatus findByEnumerator(String enumerator);
}
