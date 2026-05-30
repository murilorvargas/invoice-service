package com.invoice.invoiceservice.repositories;

import com.invoice.invoiceservice.entities.Wallet;
import com.invoice.invoiceservice.entities.WalletLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletLimitRepository extends JpaRepository<WalletLimit, Long> {

    WalletLimit findByWallet(Wallet wallet);

    List<WalletLimit> findAllByWallet(Wallet wallet);
}
