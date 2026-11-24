package com.example.portfolioapi.dto.cost;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateActualCostRequest(

        @NotNull
        LocalDate costDate,

        @NotNull
        @DecimalMin("0.00")
        @Digits(integer = 13, fraction = 2)
        BigDecimal amount,

        @Size(max = 2000)
        String description

) {
}
