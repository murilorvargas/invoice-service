package com.invoice.invoiceservice.repositories;

import com.invoice.invoiceservice.entities.CardEntryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardEntryStatusRepository extends JpaRepository<CardEntryStatus, Long> {

    CardEntryStatus findByEnumerator(String enumerator);
}
