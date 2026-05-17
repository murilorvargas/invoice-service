package com.invoice.springinvoiceservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class WalletLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "wallet_limit_key", nullable = false, unique = true, length = 36)
    private String walletLimitKey;

    @Column(name = "limit_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal limitAmount;

    @Column(name = "used_limit_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal usedLimitAmount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    protected WalletLimit() {}

    public WalletLimit(
        String walletLimitKey,
        BigDecimal limitAmount,
        BigDecimal usedLimitAmount,
        Wallet wallet
    ) {
        this.walletLimitKey = walletLimitKey;
        this.limitAmount = limitAmount;
        this.usedLimitAmount = usedLimitAmount;
        this.wallet = wallet;
    }

}
