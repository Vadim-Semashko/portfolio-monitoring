package com.example.portfolioapi.dto.portfolio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePortfolioRequest(

        @NotBlank(message = "Название обязательно")
        @Size(max = 150)
        String name,

        String description

) {
}
