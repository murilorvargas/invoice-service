package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.CardEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardEntryRepository extends JpaRepository<CardEntry, Long> {
}
