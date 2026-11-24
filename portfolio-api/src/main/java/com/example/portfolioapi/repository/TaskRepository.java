package com.example.portfolioapi.repository;

import com.example.portfolioapi.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByPlanId(Long planId);

    @Query("""
        SELECT SUM(t.plannedCost)
        FROM Task t
        WHERE t.plan.id = :planId
        """)
    BigDecimal sumPlannedCostByPlanId(
            @Param("planId") Long planId
    );
}
