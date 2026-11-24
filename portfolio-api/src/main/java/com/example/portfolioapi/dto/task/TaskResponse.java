package com.example.portfolioapi.dto.task;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TaskResponse(
        Long id,
        Long planId,
        String name,
        String description,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate,
        BigDecimal plannedCost
) {
}
