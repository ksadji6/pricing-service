package com.esmt.pricing.service;

import com.esmt.pricing.dto.FareRequest;
import com.esmt.pricing.dto.FareResponse;
import com.esmt.pricing.entity.PricingRule;
import com.esmt.pricing.entity.TransportTypeP;
import com.esmt.pricing.repository.PricingRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PricingService {

    private final PricingRuleRepository pricingRuleRepository;

    //Calcule le tarif en appliquant les remises (Heures creuses, Fidélité, Plafond).

    public FareResponse calculateFare(FareRequest request) {
        log.info("Calcul tarif: type={}, hour={}, userId={}",
                request.getTransportType(), request.getHourOfDay(), request.getUserId());

        TransportTypeP type;
        try {
            type = TransportTypeP.valueOf(request.getTransportType().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Type de transport inconnu: {}", request.getTransportType());
            return fallbackFare(request.getTransportType());
        }

        // 1. Récupération de la règle en base ou règle par défaut
        PricingRule rule = pricingRuleRepository.findByTransportTypeAndActiveTrue(type)
                .orElse(getDefaultRule(type));

        BigDecimal amount = rule.getBaseFare();
        List<String> discounts = new ArrayList<>();

        // Règle 1: Remise Heures Creuses
        if (isOffPeak(request.getHourOfDay(), rule)) {
            BigDecimal discountAmt = amount.multiply(rule.getOffPeakDiscount());
            amount = amount.subtract(discountAmt);
            discounts.add("Heure creuse -" + rule.getOffPeakDiscount().multiply(BigDecimal.valueOf(100)).intValue() + "%");
        }

        // Règle 2: Remise Fidélité (Basée sur le nombre de trajets)
        if (request.getTotalTrips() != null && request.getTotalTrips() >= rule.getLoyaltyThreshold()) {
            BigDecimal discountAmt = amount.multiply(rule.getLoyaltyDiscount());
            amount = amount.subtract(discountAmt);
            discounts.add("Fidélité -" + rule.getLoyaltyDiscount().multiply(BigDecimal.valueOf(100)).intValue() + "%");
        }

        // Règle 3: Plafond Journalier
        // Si l'utilisateur a déjà dépensé beaucoup aujourd'hui, on plafonne le prix
        if (rule.getDailyCap() != null && request.getDailySpentXof() != null) {
            BigDecimal remaining = rule.getDailyCap().subtract(BigDecimal.valueOf(request.getDailySpentXof()));
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                amount = BigDecimal.ZERO;
                discounts.add("Plafond journalier atteint → Gratuit");
            } else if (amount.compareTo(remaining) > 0) {
                amount = remaining;
                discounts.add("Plafond journalier partiel appliqué");
            }
        }

        amount = amount.setScale(2, RoundingMode.HALF_UP);
        log.info("Tarif final calculé: {} XOF, remises: {}", amount, discounts);

        return FareResponse.builder()
                .finalAmount(amount)
                .baseFare(rule.getBaseFare())
                .appliedDiscounts(discounts)
                .fallback(false)
                .build();
    }

    private boolean isOffPeak(Integer hour, PricingRule rule) {
        if (hour == null || rule.getOffPeakStart() == null || rule.getOffPeakEnd() == null) return false;
        LocalTime time = LocalTime.of(hour, 0);
        return !time.isBefore(rule.getOffPeakStart()) && time.isBefore(rule.getOffPeakEnd());
    }

    private PricingRule getDefaultRule(TransportTypeP type) {
        return PricingRule.builder()
                .transportType(type)
                .baseFare(switch (type) {
                    case BRT -> new BigDecimal("500");
                    case TER -> new BigDecimal("800"); // Ajusté selon tes prix habituels
                    case BUS -> new BigDecimal("300");
                })
                .offPeakDiscount(new BigDecimal("0.20")) // 20%
                .offPeakStart(LocalTime.of(10, 0))
                .offPeakEnd(LocalTime.of(16, 0))
                .loyaltyThreshold(50)
                .loyaltyDiscount(new BigDecimal("0.05")) // 5%
                .dailyCap(new BigDecimal("2500"))
                .build();
    }

    public FareResponse fallbackFare(String type) {
        BigDecimal amount = switch (type.toUpperCase()) {
            case "BRT" -> new BigDecimal("500");
            case "TER" -> new BigDecimal("800");
            default    -> new BigDecimal("300");
        };
        return FareResponse.builder()
                .finalAmount(amount)
                .baseFare(amount)
                .appliedDiscounts(List.of("Tarif de secours appliqué"))
                .fallback(true)
                .build();
    }
}