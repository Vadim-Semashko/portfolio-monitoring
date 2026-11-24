package com.example.portfolioapi.service;

import com.example.portfolioapi.dto.forecast.ForecastResponse;
import com.example.portfolioapi.entity.EarnedValueCalculation;
import com.example.portfolioapi.entity.Forecast;
import com.example.portfolioapi.entity.ProjectPlan;

import com.example.portfolioapi.repository.EarnedValueCalculationRepository;
import com.example.portfolioapi.repository.ForecastRepository;
import com.example.portfolioapi.repository.ProjectPlanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ForecastService {

    private static final int MONEY_SCALE = 2;

    private static final long MAX_FORECAST_DAYS = 365_000L;

    private static final BigDecimal MAX_MONEY_EXCLUSIVE = new BigDecimal("10000000000000");

    private final ProjectAccessService accessService;
    private final ProjectPlanRepository planRepository;
    private final EarnedValueCalculationRepository calculationRepository;
    private final ForecastRepository forecastRepository;

    public ForecastService(
            ProjectAccessService accessService,
            ProjectPlanRepository planRepository,
            EarnedValueCalculationRepository calculationRepository,
            ForecastRepository forecastRepository
    ) {
        this.accessService = accessService;
        this.planRepository = planRepository;
        this.calculationRepository = calculationRepository;
        this.forecastRepository = forecastRepository;
    }

    @Transactional
    public ForecastResponse calculateForecast(Long projectId, LocalDate reportDate, String username) {
        accessService.requireViewAccess(projectId, username);

        EarnedValueCalculation calculation = calculationRepository.findByProjectIdAndReportDate(projectId, reportDate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Сначала выполните расчёт EVM"));

        ProjectPlan plan = getPlan(projectId);

        BigDecimal eac = calculateEac(plan.getBudget(), calculation.getCpi());

        int plannedDuration = Math.toIntExact(ChronoUnit.DAYS.between(plan.getPlannedStartDate(), plan.getPlannedEndDate()) + 1);

        Integer estimatedDuration = calculateEstimatedDuration(plannedDuration, calculation.getSpi());

        LocalDate estimatedEndDate = null;

        if (estimatedDuration != null) {
            estimatedEndDate = plan.getPlannedStartDate().plusDays(estimatedDuration - 1L);
        }

        Forecast forecast = forecastRepository.findByCalculationId(calculation.getId())
                .orElseGet(() -> new Forecast(calculation));

        forecast.setForecastValues(eac, estimatedDuration, estimatedEndDate);

        Forecast saved = forecastRepository.save(forecast);

        return toResponse(saved, plan);
    }

    private BigDecimal calculateEac(BigDecimal bac, BigDecimal cpi) {
        if (cpi == null || cpi.signum() <= 0) {
            return null;
        }

        BigDecimal result = bac.divide(cpi, MONEY_SCALE, RoundingMode.HALF_UP);

        if (result.compareTo(MAX_MONEY_EXCLUSIVE) >= 0) {
            return null;
        }

        return result;
    }

    private Integer calculateEstimatedDuration(int plannedDuration, BigDecimal spi) {
        if (spi == null || spi.signum() <= 0) {
            return null;
        }

        BigDecimal duration = BigDecimal.valueOf(plannedDuration).divide(spi, 0, RoundingMode.CEILING);

        if (duration.compareTo(BigDecimal.valueOf(MAX_FORECAST_DAYS)) > 0) {
            return null;
        }

        return duration.intValueExact();
    }

    @Transactional(readOnly = true)
    public List<ForecastResponse> getHistory(Long projectId, String username) {
        accessService.requireViewAccess(projectId, username);

        ProjectPlan plan = getPlan(projectId);

        return forecastRepository.findHistoryByProjectId(projectId)
                .stream()
                .map(forecast -> toResponse(forecast, plan))
                .toList();
    }

    private ProjectPlan getPlan(Long projectId) {
        return planRepository.findByProjectId(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "План проекта не найден"));
    }

    private ForecastResponse toResponse(Forecast forecast, ProjectPlan plan) {
        EarnedValueCalculation calculation = forecast.getCalculation();

        int plannedDuration = Math.toIntExact(ChronoUnit.DAYS.between(plan.getPlannedStartDate(), plan.getPlannedEndDate()) + 1);

        return new ForecastResponse(
                forecast.getId(),
                calculation.getProject().getId(),
                calculation.getId(),
                calculation.getReportDate(),
                plan.getBudget(),
                calculation.getCpi(),
                calculation.getSpi(),
                forecast.getEac(),
                plannedDuration,
                forecast.getEstimatedDuration(),
                plan.getPlannedEndDate(),
                forecast.getEstimatedEndDate()
        );
    }
}
