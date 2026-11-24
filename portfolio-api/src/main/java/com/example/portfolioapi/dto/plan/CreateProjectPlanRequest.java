package com.example.portfolioapi.dto.plan;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateProjectPlanRequest(

        @NotNull
        LocalDate plannedStartDate,

        @NotNull
        LocalDate plannedEndDate

) {
}
