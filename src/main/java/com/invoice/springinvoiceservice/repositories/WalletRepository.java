package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByWalletKey(String walletKey);
}
