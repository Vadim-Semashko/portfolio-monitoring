package com.example.portfolioapi.dto.plan;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectPlanResponse(
        Long id,
        Long projectId,
        BigDecimal budget,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate
) {
}
