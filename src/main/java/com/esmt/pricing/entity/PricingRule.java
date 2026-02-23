package com.esmt.pricing.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "pricing_rules")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class PricingRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "transport_type", nullable = false, unique = true)
    private TransportTypeP transportType;

    @Column(name = "base_fare", nullable = false, precision = 8, scale = 2)
    private BigDecimal baseFare;

    @Column(name = "off_peak_discount", precision = 4, scale = 2)
    @Builder.Default
    private BigDecimal offPeakDiscount = BigDecimal.ZERO;

    @Column(name = "off_peak_start")
    private LocalTime offPeakStart;

    @Column(name = "off_peak_end")
    private LocalTime offPeakEnd;

    @Column(name = "loyalty_threshold")
    @Builder.Default
    private int loyaltyThreshold = 50;

    @Column(name = "loyalty_discount", precision = 4, scale = 2)
    @Builder.Default
    private BigDecimal loyaltyDiscount = BigDecimal.ZERO;

    @Column(name = "daily_cap", precision = 8, scale = 2)
    private BigDecimal dailyCap; // Plafond journalier (ex: 2000 XOF max par jour)

    @Builder.Default
    private boolean active = true;

    @Column(name = "valid_from")
    private LocalDate validFrom;
}