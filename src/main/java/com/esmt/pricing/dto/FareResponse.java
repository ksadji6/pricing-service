package com.esmt.pricing.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder
public class FareResponse {
    private BigDecimal finalAmount;      // Le prix final à débiter
    private BigDecimal baseFare;         // Le prix catalogue (avant remise)
    private List<String> appliedDiscounts; // Ex: ["Heure creuse -20%", "Fidélité -5%"]
    private boolean fallback;            // Indique si c'est un prix de secours
}