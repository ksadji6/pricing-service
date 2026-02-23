package com.esmt.pricing.dto;

import lombok.Data;

@Data
public class FareRequest {
    private Long userId;
    private String transportType;
    private Integer hourOfDay;
    private Integer totalTrips;      // Utilisé pour la règle "Fidélité"
    private Double dailySpentXof;    // Utilisé pour la règle "Plafond Journalier"
}