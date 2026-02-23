package com.esmt.pricing.repository;

import com.esmt.pricing.entity.FareCalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FareCalculationRepository extends JpaRepository<FareCalculation, Long> {
    // Repository standard pour FareCalculation
}