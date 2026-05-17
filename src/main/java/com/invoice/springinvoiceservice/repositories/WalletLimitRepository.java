package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.WalletLimit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletLimitRepository extends JpaRepository<WalletLimit, Long> {
}
