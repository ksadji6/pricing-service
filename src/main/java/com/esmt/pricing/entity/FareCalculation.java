package com.esmt.pricing.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fare_calculations")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class FareCalculation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "base_fare", precision = 8, scale = 2)
    private BigDecimal baseFare;

    @Column(name = "off_peak_discount_amt", precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal offPeakDiscountAmt = BigDecimal.ZERO;

    @Column(name = "loyalty_discount_amt", precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal loyaltyDiscountAmt = BigDecimal.ZERO;

    @Column(name = "final_amount", nullable = false, precision = 8, scale = 2)
    private BigDecimal finalAmount;

    @Column(name = "is_fallback")
    @Builder.Default
    private boolean isFallback = false;

    @Column(name = "calculated_at")
    @Builder.Default
    private LocalDateTime calculatedAt = LocalDateTime.now();
}