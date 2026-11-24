
package com.example.portfolioapi.service;

import com.example.portfolioapi.dto.evm.EarnedValueResponse;
import com.example.portfolioapi.entity.EarnedValueCalculation;
import com.example.portfolioapi.entity.Project;
import com.example.portfolioapi.entity.ProjectPlan;
import com.example.portfolioapi.entity.Task;
import com.example.portfolioapi.entity.TaskProgress;

import com.example.portfolioapi.repository.ActualCostRepository;
import com.example.portfolioapi.repository.EarnedValueCalculationRepository;
import com.example.portfolioapi.repository.ProjectPlanRepository;
import com.example.portfolioapi.repository.TaskProgressRepository;
import com.example.portfolioapi.repository.TaskRepository;
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
public class EarnedValueService {

    private static final int MONEY_SCALE = 2;
    private static final int INDEX_SCALE = 4;

    private final ProjectAccessService accessService;
    private final ProjectPlanRepository planRepository;
    private final TaskRepository taskRepository;
    private final TaskProgressRepository progressRepository;
    private final ActualCostRepository costRepository;
    private final EarnedValueCalculationRepository calculationRepository;

    public EarnedValueService(
            ProjectAccessService accessService,
            ProjectPlanRepository planRepository,
            TaskRepository taskRepository,
            TaskProgressRepository progressRepository,
            ActualCostRepository costRepository,
            EarnedValueCalculationRepository calculationRepository
    ) {
        this.accessService = accessService;
        this.planRepository = planRepository;
        this.taskRepository = taskRepository;
        this.progressRepository = progressRepository;
        this.costRepository = costRepository;
        this.calculationRepository = calculationRepository;
    }

    @Transactional
    public EarnedValueResponse calculate(Long projectId, LocalDate reportDate, String username) {
        Project project = accessService.requireViewAccess(projectId, username);

        if (reportDate.isBefore(project.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Отчётная дата раньше начала проекта");
        }

        if (reportDate.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя рассчитывать фактические показатели на будущую дату");
        }

        ProjectPlan plan = planRepository
                .findByProjectId(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "План проекта не найден"));

        List<Task> tasks = taskRepository.findByPlanId(plan.getId());

        if (tasks.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "В плане проекта отсутствуют работы");
        }

        BigDecimal pv = BigDecimal.ZERO;
        BigDecimal ev = BigDecimal.ZERO;

        for (Task task : tasks) {
            pv = pv.add(calculateTaskPv(task, reportDate));
            ev = ev.add(calculateTaskEv(task, reportDate));
        }

        BigDecimal ac = costRepository.sumCostsByProjectAndDate(projectId, reportDate);

        if (ac == null) {
            ac = BigDecimal.ZERO;
        }

        pv = roundMoney(pv);
        ev = roundMoney(ev);
        ac = roundMoney(ac);

        BigDecimal cv = ev.subtract(ac);
        BigDecimal sv = ev.subtract(pv);

        BigDecimal cpi = safeDivide(ev, ac);
        BigDecimal spi = safeDivide(ev, pv);

        EarnedValueCalculation calculation = calculationRepository
                .findByProjectIdAndReportDate(projectId, reportDate)
                .orElseGet(() -> new EarnedValueCalculation(project, reportDate));

        calculation.setIndicators(pv, ev, ac, cv, sv, cpi, spi);

        EarnedValueCalculation saved = calculationRepository.save(calculation);

        return toResponse(saved, plan.getBudget());
    }

    private BigDecimal calculateTaskPv(Task task, LocalDate reportDate) {
        LocalDate start = task.getPlannedStartDate();
        LocalDate end = task.getPlannedEndDate();

        if (reportDate.isBefore(start)) {
            return BigDecimal.ZERO;
        }

        if (!reportDate.isBefore(end)) {
            return task.getPlannedCost();
        }

        long totalDays = ChronoUnit.DAYS.between(start, end) + 1;

        long elapsedDays = ChronoUnit.DAYS.between(start, reportDate) + 1;

        return task.getPlannedCost()
                .multiply(BigDecimal.valueOf(elapsedDays))
                .divide(
                        BigDecimal.valueOf(totalDays),
                        MONEY_SCALE,
                        RoundingMode.HALF_UP
                );
    }

    private BigDecimal calculateTaskEv(Task task, LocalDate reportDate) {
        BigDecimal completion = progressRepository
                .findFirstByTaskIdAndReportDateLessThanEqualOrderByReportDateDesc(
                        task.getId(), reportDate
                )
                .map(TaskProgress::getCompletionPercent)
                .orElse(BigDecimal.ZERO);

        return task.getPlannedCost()
                .multiply(completion)
                .divide(
                        BigDecimal.valueOf(100),
                        MONEY_SCALE,
                        RoundingMode.HALF_UP
                );
    }

    private BigDecimal safeDivide(BigDecimal numerator, BigDecimal denominator) {
        if (denominator.signum() == 0) {
            return null;
        }

        return numerator.divide(
                denominator,
                INDEX_SCALE,
                RoundingMode.HALF_UP
        );
    }

    private BigDecimal roundMoney(BigDecimal value) {
        return value.setScale(
                MONEY_SCALE,
                RoundingMode.HALF_UP
        );
    }

    @Transactional(readOnly = true)
    public List<EarnedValueResponse> getHistory(Long projectId, String username) {
        accessService.requireViewAccess(projectId, username);

        BigDecimal bac = planRepository.findByProjectId(projectId)
                .map(ProjectPlan::getBudget)
                .orElse(BigDecimal.ZERO);

        return calculationRepository
                .findByProjectIdOrderByReportDateDesc(projectId)
                .stream()
                .map(calculation -> toResponse(calculation, bac))
                .toList();
    }

    private EarnedValueResponse toResponse(EarnedValueCalculation calculation, BigDecimal bac) {
        return new EarnedValueResponse(
                calculation.getId(),
                calculation.getProject().getId(),
                calculation.getReportDate(),
                bac,
                calculation.getPv(),
                calculation.getEv(),
                calculation.getAc(),
                calculation.getCv(),
                calculation.getSv(),
                calculation.getCpi(),
                calculation.getSpi()
        );
    }
}
