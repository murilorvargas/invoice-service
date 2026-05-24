package com.invoice.springinvoiceservice.repositories;

import com.invoice.springinvoiceservice.entities.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByWalletKey(String walletKey);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.walletKey = :walletKey")
    Optional<Wallet> findByWalletKeyForUpdate(@Param("walletKey") String walletKey);
}
