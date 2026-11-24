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
@Table(name = "task_progress")
public class TaskProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "completion_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal completionPercent;

    protected TaskProgress() {
    }

    public TaskProgress(Task task, LocalDate reportDate, BigDecimal completionPercent) {
        this.task = task;
        this.reportDate = reportDate;
        this.completionPercent = completionPercent;
    }

    public Long getId() {
        return id;
    }

    public Task getTask() {
        return task;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public BigDecimal getCompletionPercent() {
        return completionPercent;
    }

    public void setCompletionPercent(BigDecimal completionPercent) {
        this.completionPercent = completionPercent;
    }
}
