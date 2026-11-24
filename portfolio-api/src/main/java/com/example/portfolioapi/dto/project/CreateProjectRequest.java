package com.example.portfolioapi.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateProjectRequest(

        @NotNull
        Long portfolioId,

        @NotNull
        Long managerId,

        @NotBlank
        @Size(max = 150)
        String name,

        String description,

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate

) {
}
