package com.invoice.invoiceservice.repositories;

import com.invoice.invoiceservice.entities.CardEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardEntryRepository extends JpaRepository<CardEntry, Long> {

    Optional<CardEntry> findByCardEntryKey(String cardEntryKey);
}
