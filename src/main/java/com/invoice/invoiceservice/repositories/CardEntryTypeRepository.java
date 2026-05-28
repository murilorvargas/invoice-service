package com.invoice.invoiceservice.repositories;

import com.invoice.invoiceservice.entities.CardEntryType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardEntryTypeRepository extends JpaRepository<CardEntryType, Long> {

    CardEntryType findByEnumerator(String enumerator);
}
