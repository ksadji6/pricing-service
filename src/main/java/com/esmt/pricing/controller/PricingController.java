package com.esmt.pricing.controller;

import com.esmt.pricing.dto.FareRequest;
import com.esmt.pricing.dto.FareResponse;
import com.esmt.pricing.entity.PricingRule;
import com.esmt.pricing.service.PricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Pricing Service", description = "Moteur de calcul des tarifs et gestion des remises")
public class PricingController {

    private final PricingService pricingService;
    @Value("${server.port}")
    private String port;

    //calcul de tarif. Consommé par le Trip Service via Feign Client.
    @PostMapping("/calculate")
    @Operation(summary = "Calculer le tarif d'un trajet en appliquant les règles métier (Peak/Off-peak, Fidélité, Cap)")
    public ResponseEntity<FareResponse> calculateFare(@RequestBody FareRequest request) {
        log.info("Requête de calcul reçue sur l'instance tournant sur le port : {}", port);
        // Le service retourne un FareResponse contenant le montant final et les détails des remises
        return ResponseEntity.ok(pricingService.calculateFare(request));
    }

}