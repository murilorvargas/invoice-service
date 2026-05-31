package com.invoice.invoiceservice.repositories;

import com.invoice.invoiceservice.entities.CardEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CardEntryRepository extends JpaRepository<CardEntry, Long>, JpaSpecificationExecutor<CardEntry> {

    Optional<CardEntry> findByCardEntryKey(String cardEntryKey);
}
