package com.example.portfolioapi.repository;

import com.example.portfolioapi.entity.Forecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForecastRepository extends JpaRepository<Forecast, Long> {
    Optional<Forecast> findByCalculationId(Long calculationId);
}
