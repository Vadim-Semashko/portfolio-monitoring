package com.example.portfolioapi.dto.forecast;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ForecastResponse(
        Long id,
        Long projectId,
        Long calculationId,
        LocalDate reportDate,

        BigDecimal bac,
        BigDecimal cpi,
        BigDecimal spi,

        BigDecimal eac,
        Integer plannedDuration,
        Integer estimatedDuration,
        LocalDate plannedEndDate,
        LocalDate estimatedEndDate
) {
}
