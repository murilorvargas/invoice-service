package com.invoice.invoiceservice.repositories;

import com.invoice.invoiceservice.entities.WalletStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletStatusRepository extends JpaRepository<WalletStatus, Long> {

    WalletStatus findByEnumerator(String enumerator);
}
