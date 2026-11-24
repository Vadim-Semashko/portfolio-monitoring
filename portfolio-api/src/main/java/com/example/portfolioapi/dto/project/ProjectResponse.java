package com.example.portfolioapi.dto.project;

import com.example.portfolioapi.entity.ProjectStatus;

import java.time.LocalDate;

public record ProjectResponse(
        Long id,
        Long portfolioId,
        String portfolioName,
        Long managerId,
        String managerName,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        ProjectStatus status
) {
}
