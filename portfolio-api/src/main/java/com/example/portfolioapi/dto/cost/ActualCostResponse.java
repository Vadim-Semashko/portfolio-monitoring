package com.example.portfolioapi.dto.cost;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ActualCostResponse(
        Long id,
        Long taskId,
        LocalDate costDate,
        BigDecimal amount,
        String description
) {
}
