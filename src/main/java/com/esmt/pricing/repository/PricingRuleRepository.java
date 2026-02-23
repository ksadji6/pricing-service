package com.esmt.pricing.repository;

import com.esmt.pricing.entity.PricingRule;
import com.esmt.pricing.entity.TransportTypeP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {

    // Pour trouver la règle active (ex: BUS à 300 XOF)
    Optional<PricingRule> findByTransportTypeAndActiveTrue(TransportTypeP type);
}