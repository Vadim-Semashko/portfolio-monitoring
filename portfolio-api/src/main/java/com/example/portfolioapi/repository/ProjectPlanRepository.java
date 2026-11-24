package com.example.portfolioapi.repository;

import com.example.portfolioapi.entity.ProjectPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectPlanRepository extends JpaRepository<ProjectPlan, Long> {
    Optional<ProjectPlan> findByProjectId(Long projectId);
    boolean existsByProjectId(Long projectId);
}
