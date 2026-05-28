package com.invoice.invoiceservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "invoice_key", nullable = false, unique = true, length = 36)
    private String invoiceKey;

    @Column(name = "closing_date", nullable = false, columnDefinition = "DATE")
    private LocalDate closingDate;

    @Column(name = "due_date", nullable = false, columnDefinition = "DATE")
    private LocalDate dueDate;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

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
    @JoinColumn(name = "invoice_status_id", nullable = false)
    private InvoiceStatus invoiceStatus;

    public Invoice() {}

    public Invoice (
        String invoiceKey,
        LocalDate closingDate,
        LocalDate dueDate,
        BigDecimal amount,
        Wallet wallet,
        InvoiceStatus invoiceStatus
    ) {
        this.invoiceKey = invoiceKey;
        this.closingDate = closingDate;
        this.dueDate = dueDate;
        this.amount = amount;
        this.wallet = wallet;
        this.invoiceStatus = invoiceStatus;
    }
}
