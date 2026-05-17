package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.CardEntryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardEntryStatusRepository extends JpaRepository<CardEntryStatus, Long> {

    CardEntryStatus findByEnumerator(String enumerator);
}
