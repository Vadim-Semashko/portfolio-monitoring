package com.example.portfolioapi.dto.progress;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TaskProgressResponse(
        Long id,
        Long taskId,
        LocalDate reportDate,
        BigDecimal completionPercent
) {
}
