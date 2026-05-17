package com.invoice.springinvoiceservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class InvoiceConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "invoice_configuration_key", nullable = false, unique = true, length = 36)
    private String invoiceConfigurationKey;

    @Column(name = "closing_fixed_day", nullable = false)
    private Integer closingFixedDay;

    @Column(name = "due_fixed_day", nullable = true)
    private Integer dueFixedDay;

    @Column(name = "due_offset_months", nullable = true)
    private Integer dueOffsetMonths;

    @Column(name = "due_days_after_closing", nullable = true)
    private Integer dueDaysAfterClosing;

    @Column(name = "fine_percentage", nullable = false, precision = 5, scale = 4)
    private BigDecimal finePercentage;

    @Column(name = "interest_percentage", nullable = false, precision = 5, scale = 4)
    private BigDecimal interestPercentage;

    @Column(name = "revolving_interest_percentage", nullable = false, precision = 5, scale = 4)
    private BigDecimal revolvingInterestPercentage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP(3)")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "due_type_id", nullable = false)
    private DueType dueType;

    protected InvoiceConfiguration() {}

    public InvoiceConfiguration(
        String invoiceConfigurationKey,
        Integer closingFixedDay,
        Integer dueFixedDay,
        Integer dueOffsetMonths,
        Integer dueDaysAfterClosing,
        BigDecimal finePercentage,
        BigDecimal interestPercentage,
        BigDecimal revolvingInterestPercentage,
        DueType dueType
    ) {
        this.invoiceConfigurationKey = invoiceConfigurationKey;
        this.closingFixedDay = closingFixedDay;
        this.dueFixedDay = dueFixedDay;
        this.dueOffsetMonths = dueOffsetMonths;
        this.dueDaysAfterClosing = dueDaysAfterClosing;
        this.finePercentage = finePercentage;
        this.interestPercentage = interestPercentage;
        this.revolvingInterestPercentage = revolvingInterestPercentage;
        this.dueType = dueType;
    }

}
