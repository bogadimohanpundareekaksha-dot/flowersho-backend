package com.example.FLOWER.SHOP.BILLING.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "uploaded_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadedImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Bill bill;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String url;

    private String description;

    @Column(nullable = false)
    private Instant uploadedAt;

    @PrePersist
    public void prePersist() {
        if (uploadedAt == null) {
            uploadedAt = Instant.now();
        }
    }
}
