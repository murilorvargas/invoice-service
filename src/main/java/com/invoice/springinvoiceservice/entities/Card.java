package com.invoice.springinvoiceservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"wallet_id", "request_control_key"})
})
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "card_key", nullable = false, unique = true, length = 36)
    private String cardKey;

    @Column(name = "request_control_key", nullable = false, length = 36)
    private String requestControlKey;

    @Column(name = "document_number", nullable = false, length = 14)
    private String documentNumber;

    @Column(name = "limit_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlyLimitAmount;

    @Column(name = "used_limit_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal usedMonthlyLimitAmount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_status_id", nullable = false)
    private CardStatus cardStatus;

    protected Card() {}

    public Card(
        String cardKey,
        String requestControlKey,
        String documentNumber,
        BigDecimal monthlyLimitAmount,
        BigDecimal usedMonthlyLimitAmount,
        Wallet wallet,
        CardStatus cardStatus
    ) {
        this.cardKey = cardKey;
        this.requestControlKey = requestControlKey;
        this.documentNumber = documentNumber;
        this.monthlyLimitAmount = monthlyLimitAmount;
        this.usedMonthlyLimitAmount = usedMonthlyLimitAmount;
        this.wallet = wallet;
        this.cardStatus = cardStatus;
    }
}
