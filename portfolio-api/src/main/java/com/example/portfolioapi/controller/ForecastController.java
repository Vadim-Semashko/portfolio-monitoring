package com.example.portfolioapi.controller;

import com.example.portfolioapi.dto.forecast.ForecastResponse;
import com.example.portfolioapi.service.ForecastService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/forecasts")
public class ForecastController {

    private final ForecastService forecastService;

    public ForecastController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @PostMapping("/calculate")
    public ForecastResponse calculate(
            @PathVariable Long projectId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate reportDate,

            Authentication authentication
    ) {
        return forecastService.calculateForecast(projectId, reportDate, authentication.getName());
    }

    @GetMapping
    public List<ForecastResponse> getHistory(@PathVariable Long projectId, Authentication authentication) {
        return forecastService.getHistory(projectId, authentication.getName());
    }
}
