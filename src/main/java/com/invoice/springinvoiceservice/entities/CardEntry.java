package com.invoice.springinvoiceservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class CardEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "card_entry_key", nullable = false, unique = true, length = 36)
    private String cardEntryKey;

    @Column(name = "request_control_key", nullable = false, unique = true, length = 36)
    private String requestControlKey;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "number_of_installments", nullable = false)
    private Integer numberOfInstallments;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "card_entry_data", nullable = false, columnDefinition = "JSON")
    private CardEntryData cardEntryData;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_entry_type_id", nullable = false)
    private CardEntryType cardEntryType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_entry_status_id", nullable = false)
    private CardEntryStatus cardEntryStatus;

    protected CardEntry() {}

    public CardEntry(
        String cardEntryKey,
        String requestControlKey,
        BigDecimal amount,
        Integer numberOfInstallments,
        CardEntryData cardEntryData,
        Card card,
        CardEntryStatus cardEntryStatus,
        CardEntryType cardEntryType
    ) {
        this.cardEntryKey = cardEntryKey;
        this.requestControlKey = requestControlKey;
        this.amount = amount;
        this.numberOfInstallments = numberOfInstallments;
        this.cardEntryData = cardEntryData;
        this.card = card;
        this.cardEntryStatus = cardEntryStatus;
        this.cardEntryType = cardEntryType;
    }
}
