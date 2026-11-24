package com.example.portfolioapi.repository;

import com.example.portfolioapi.entity.ActualCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ActualCostRepository extends JpaRepository<ActualCost, Long> {
    List<ActualCost> findByTaskIdOrderByCostDateAsc(Long taskId);

    @Query("""
        SELECT SUM(c.amount)
        FROM ActualCost c
        WHERE c.task.id = :taskId
          AND c.costDate <= :reportDate
        """)
    BigDecimal sumCostsByTaskAndDate(@Param("taskId") Long taskId, @Param("reportDate") LocalDate reportDate);

    @Query("""
        SELECT SUM(c.amount)
        FROM ActualCost c
        WHERE c.task.plan.project.id = :projectId
          AND c.costDate <= :reportDate
        """)
    BigDecimal sumCostsByProjectAndDate(@Param("projectId") Long projectId, @Param("reportDate") LocalDate reportDate);
}
