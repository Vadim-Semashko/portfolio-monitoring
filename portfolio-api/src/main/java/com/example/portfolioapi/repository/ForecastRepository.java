package com.example.portfolioapi.repository;

import com.example.portfolioapi.entity.Forecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ForecastRepository extends JpaRepository<Forecast, Long> {
    Optional<Forecast> findByCalculationId(Long calculationId);

    @Query("""
    SELECT f
    FROM Forecast f
    JOIN FETCH f.calculation c
    WHERE c.project.id = :projectId
    ORDER BY c.reportDate DESC
    """)
    List<Forecast> findHistoryByProjectId(@Param("projectId") Long projectId);

}
