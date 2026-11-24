package com.example.portfolioapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "earned_value_calculations")
public class EarnedValueCalculation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal pv;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal ev;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal ac;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal cv;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal sv;

    @Column(precision = 12, scale = 4)
    private BigDecimal cpi;

    @Column(precision = 12, scale = 4)
    private BigDecimal spi;

    protected EarnedValueCalculation() {
    }

    public EarnedValueCalculation(Project project, LocalDate reportDate) {
        this.project = project;
        this.reportDate = reportDate;
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public BigDecimal getPv() {
        return pv;
    }

    public BigDecimal getEv() {
        return ev;
    }

    public BigDecimal getAc() {
        return ac;
    }

    public BigDecimal getCv() {
        return cv;
    }

    public BigDecimal getSv() {
        return sv;
    }

    public BigDecimal getCpi() {
        return cpi;
    }

    public BigDecimal getSpi() {
        return spi;
    }

    public void setIndicators(BigDecimal pv, BigDecimal ev, BigDecimal ac, BigDecimal cv, BigDecimal sv, BigDecimal cpi,
                              BigDecimal spi) {
        this.pv = pv;
        this.ev = ev;
        this.ac = ac;
        this.cv = cv;
        this.sv = sv;
        this.cpi = cpi;
        this.spi = spi;
    }
}
