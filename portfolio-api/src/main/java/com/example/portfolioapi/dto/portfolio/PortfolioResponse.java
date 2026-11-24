package com.example.portfolioapi.dto.portfolio;

public record PortfolioResponse(
        Long id,
        String name,
        String description,
        Long managerId,
        String managerName
) {
}
