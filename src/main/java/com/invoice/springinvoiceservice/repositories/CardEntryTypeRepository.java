package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.CardEntryType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardEntryTypeRepository extends JpaRepository<CardEntryType, Long> {

    CardEntryType findByEnumerator(String enumerator);
}
