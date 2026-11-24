package com.example.portfolioapi.repository;

import com.example.portfolioapi.entity.Portfolio;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    @EntityGraph(attributePaths = "manager")
    List<Portfolio> findByManagerId(Long managerId);

    @EntityGraph(attributePaths = "manager")
    List<Portfolio> findAllBy();

    @Override
    @EntityGraph(attributePaths = "manager")
    Optional<Portfolio> findById(Long id);
}
