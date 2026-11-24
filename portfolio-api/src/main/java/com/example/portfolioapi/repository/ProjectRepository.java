package com.example.portfolioapi.repository;

import com.example.portfolioapi.entity.Project;
import com.example.portfolioapi.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByPortfolioId(Long portfolioId);
    List<Project> findByManagerId(Long managerId);
    List<Project> findByStatus(ProjectStatus status);
    List<Project> findByPortfolioIdAndStatus(Long portfolioId, ProjectStatus status);
}
