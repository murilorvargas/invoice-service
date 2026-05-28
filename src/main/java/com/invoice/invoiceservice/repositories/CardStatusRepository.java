package com.invoice.invoiceservice.repositories;

import com.invoice.invoiceservice.entities.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardStatusRepository extends JpaRepository<CardStatus, Long> {

    CardStatus findByEnumerator(String enumerator);
}
