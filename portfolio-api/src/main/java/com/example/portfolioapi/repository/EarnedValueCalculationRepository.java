package com.example.portfolioapi.repository;

import com.example.portfolioapi.entity.EarnedValueCalculation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EarnedValueCalculationRepository extends JpaRepository<EarnedValueCalculation, Long> {
    Optional<EarnedValueCalculation> findByProjectIdAndReportDate(Long projectId, LocalDate reportDate);
    List<EarnedValueCalculation> findByProjectIdOrderByReportDateDesc(Long projectId);
    Optional<EarnedValueCalculation> findFirstByProjectIdOrderByReportDateDesc(Long projectId);
}
