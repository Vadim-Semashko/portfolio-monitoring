package com.example.portfolioapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "forecasts")
public class Forecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "calculation_id", nullable = false, unique = true)
    private EarnedValueCalculation calculation;

    @Column(precision = 15, scale = 2)
    private BigDecimal eac;

    @Column(name = "estimated_duration")
    private Integer estimatedDuration;

    @Column(name = "estimated_end_date")
    private LocalDate estimatedEndDate;

    protected Forecast() {
    }

    public Forecast(EarnedValueCalculation calculation) {
        this.calculation = calculation;
    }

    public Long getId() {
        return id;
    }

    public EarnedValueCalculation getCalculation() {
        return calculation;
    }

    public BigDecimal getEac() {
        return eac;
    }

    public Integer getEstimatedDuration() {
        return estimatedDuration;
    }

    public LocalDate getEstimatedEndDate() {
        return estimatedEndDate;
    }

    public void setForecastValues(BigDecimal eac, Integer estimatedDuration, LocalDate estimatedEndDate) {
        this.eac = eac;
        this.estimatedDuration = estimatedDuration;
        this.estimatedEndDate = estimatedEndDate;
    }
}
