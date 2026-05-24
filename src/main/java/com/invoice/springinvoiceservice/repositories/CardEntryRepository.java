package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.CardEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardEntryRepository extends JpaRepository<CardEntry, Long> {

    Optional<CardEntry> findByCardEntryKey(String cardEntryKey);
}
