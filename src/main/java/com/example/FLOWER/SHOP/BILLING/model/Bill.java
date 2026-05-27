package com.example.FLOWER.SHOP.BILLING.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private Double paidAmount;

    @Column(nullable = false)
    private Double dueAmount;

    @Column(nullable = false)
    private LocalDate billDate;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "bill_image_url")
    private String billImageUrl;

    private String notes;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
