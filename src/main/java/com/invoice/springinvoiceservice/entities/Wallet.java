package com.invoice.springinvoiceservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"requester_key", "request_control_key"}),
    @UniqueConstraint(columnNames = {"requester_key", "document_number"})
})
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "wallet_key", nullable = false, unique = true, length = 36)
    private String walletKey;

    @Column(name = "requester_key", nullable = false, length = 36)
    private String requesterKey;

    @Column(name = "request_control_key", nullable = false, length = 36)
    private String requestControlKey;

    @Column(name = "document_number", nullable = false, length = 14)
    private String documentNumber;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_configuration_id", nullable = false)
    private InvoiceConfiguration invoiceConfiguration;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_status_id", nullable = false)
    private WalletStatus walletStatus;

    protected Wallet() {}

    public Wallet(
        String walletKey,
        String requesterKey,
        String requestControlKey,
        String documentNumber,
        InvoiceConfiguration invoiceConfiguration,
        WalletStatus walletStatus
    ) {
        this.walletKey = walletKey;
        this.requesterKey = requesterKey;
        this.requestControlKey = requestControlKey;
        this.documentNumber = documentNumber;
        this.invoiceConfiguration = invoiceConfiguration;
        this.walletStatus = walletStatus;
    }

}
