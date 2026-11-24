package com.example.portfolioapi.service;

import com.example.portfolioapi.dto.cost.ActualCostResponse;
import com.example.portfolioapi.dto.cost.CreateActualCostRequest;
import com.example.portfolioapi.entity.ActualCost;
import com.example.portfolioapi.entity.Task;
import com.example.portfolioapi.repository.ActualCostRepository;
import com.example.portfolioapi.repository.TaskRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class ActualCostService {

    private final ActualCostRepository costRepository;
    private final TaskRepository taskRepository;
    private final ProjectAccessService accessService;

    public ActualCostService(ActualCostRepository costRepository, TaskRepository taskRepository, ProjectAccessService accessService) {
        this.costRepository = costRepository;
        this.taskRepository = taskRepository;
        this.accessService = accessService;
    }

    @Transactional
    public ActualCostResponse createCost(Long taskId, CreateActualCostRequest request, String username) {
        Task task = getTask(taskId);

        accessService.requireManageAccess(task.getPlan().getProject().getId(), username);

        validateDate(request.costDate(), task.getPlan().getProject().getStartDate());

        ActualCost cost = new ActualCost(
                task,
                request.costDate(),
                request.amount(),
                request.description()
        );

        ActualCost saved = costRepository.save(cost);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ActualCostResponse> getCostHistory(Long taskId, String username) {
        Task task = getTask(taskId);

        accessService.requireViewAccess(task.getPlan().getProject().getId(), username);

        return costRepository
                .findByTaskIdOrderByCostDateAsc(taskId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Task getTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Работа не найдена"));
    }

    private void validateDate(LocalDate costDate, LocalDate projectStartDate
    ) {
        if (costDate.isBefore(projectStartDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата затрат раньше начала проекта");
        }

        if (costDate.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя регистрировать затраты на будущую дату");
        }
    }

    private ActualCostResponse toResponse(ActualCost cost) {
        return new ActualCostResponse(
                cost.getId(),
                cost.getTask().getId(),
                cost.getCostDate(),
                cost.getAmount(),
                cost.getDescription()
        );
    }
}
