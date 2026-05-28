package com.invoice.invoiceservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "invoice_item_key", nullable = false, unique = true, length = 36)
    private String invoiceItemKey;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_item_status_id", nullable = false)
    private InvoiceItemStatus invoiceItemStatus;

    protected InvoiceItem() {}

    public InvoiceItem(
        String invoiceItemKey,
        String description,
        BigDecimal amount,
        Invoice invoice,
        InvoiceItemStatus invoiceItemStatus
    ) {
        this.invoiceItemKey = invoiceItemKey;
        this.description = description;
        this.amount = amount;
        this.invoice = invoice;
        this.invoiceItemStatus = invoiceItemStatus;
    }
}
