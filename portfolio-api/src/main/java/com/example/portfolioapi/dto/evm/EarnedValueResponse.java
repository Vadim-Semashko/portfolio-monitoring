package com.example.portfolioapi.dto.evm;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EarnedValueResponse(
        Long id,
        Long projectId,
        LocalDate reportDate,

        BigDecimal bac,
        BigDecimal pv,
        BigDecimal ev,
        BigDecimal ac,

        BigDecimal cv,
        BigDecimal sv,
        BigDecimal cpi,
        BigDecimal spi
) {
}
