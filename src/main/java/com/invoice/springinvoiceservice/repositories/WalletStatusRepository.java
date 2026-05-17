package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.WalletStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletStatusRepository extends JpaRepository<WalletStatus, Long> {

    WalletStatus findByEnumerator(String enumerator);
}
