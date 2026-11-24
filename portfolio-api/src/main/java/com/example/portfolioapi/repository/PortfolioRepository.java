package com.example.portfolioapi.repository;

import com.example.portfolioapi.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByManagerId(Long managerId);

}
