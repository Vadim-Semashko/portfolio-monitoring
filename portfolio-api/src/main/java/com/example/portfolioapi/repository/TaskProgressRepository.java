package com.example.portfolioapi.repository;

import com.example.portfolioapi.entity.TaskProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskProgressRepository extends JpaRepository<TaskProgress, Long> {
    List<TaskProgress> findByTaskIdOrderByReportDateAsc(Long taskId);
    Optional<TaskProgress> findByTaskIdAndReportDate(Long taskId, LocalDate reportDate);
    Optional<TaskProgress> findFirstByTaskIdAndReportDateLessThanEqualOrderByReportDateDesc(Long taskId, LocalDate reportDate);
}
