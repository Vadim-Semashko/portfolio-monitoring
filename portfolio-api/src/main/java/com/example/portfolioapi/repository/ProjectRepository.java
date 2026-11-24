package com.example.portfolioapi.repository;

import com.example.portfolioapi.entity.Project;
import com.example.portfolioapi.entity.ProjectStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @EntityGraph(attributePaths = {"portfolio", "manager"})
    List<Project> findByPortfolioId(Long portfolioId);

    @EntityGraph(attributePaths = {"portfolio", "manager"})
    List<Project> findByManagerId(Long managerId);

    @EntityGraph(attributePaths = {"portfolio", "manager"})
    List<Project> findByStatus(ProjectStatus status);

    @EntityGraph(attributePaths = {"portfolio", "manager"})
    List<Project> findByPortfolioIdAndStatus(Long portfolioId, ProjectStatus status);

    @EntityGraph(attributePaths = {"portfolio", "manager"})
    @Query("""
        SELECT p
        FROM Project p
        WHERE p.portfolio.manager.id = :managerId
        """)
    List<Project> findByPortfolioManagerId(@Param("managerId") Long managerId);

    @EntityGraph(attributePaths = {"portfolio", "manager"})
    List<Project> findAllBy();

    @Override
    @EntityGraph(attributePaths = {"portfolio", "manager"})
    Optional<Project> findById(Long id);
}
