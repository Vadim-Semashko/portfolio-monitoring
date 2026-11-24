package com.example.portfolioapi.dto.task;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTaskRequest(

        @NotBlank
        @Size(max = 150)
        String name,

        String description,

        @NotNull
        LocalDate plannedStartDate,

        @NotNull
        LocalDate plannedEndDate,

        @NotNull
        @DecimalMin("0.00")
        @Digits(integer = 13, fraction = 2)
        BigDecimal plannedCost

) {
}
