package com.example.portfolioapi.repository;

import com.example.portfolioapi.entity.ProjectPlan;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectPlanRepository extends JpaRepository<ProjectPlan, Long> {
    Optional<ProjectPlan> findByProjectId(Long projectId);
    boolean existsByProjectId(Long projectId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProjectPlan p WHERE p.id = :id")
    Optional<ProjectPlan> findByIdForUpdate(@Param("id") Long id);

}
