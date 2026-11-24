package com.example.portfolioapi.service;

import com.example.portfolioapi.dto.task.CreateTaskRequest;
import com.example.portfolioapi.dto.task.TaskResponse;
import com.example.portfolioapi.entity.ProjectPlan;
import com.example.portfolioapi.entity.Task;
import com.example.portfolioapi.repository.ProjectPlanRepository;
import com.example.portfolioapi.repository.TaskRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectPlanRepository planRepository;
    private final ProjectAccessService accessService;

    public TaskService(TaskRepository taskRepository, ProjectPlanRepository planRepository, ProjectAccessService accessService) {
        this.taskRepository = taskRepository;
        this.planRepository = planRepository;
        this.accessService = accessService;
    }

    @Transactional
    public TaskResponse createTask(Long planId, CreateTaskRequest request, String username) {
        ProjectPlan plan = planRepository.findByIdForUpdate(planId)
                .orElseThrow(this::planNotFound);

        accessService.requireManageAccess(plan.getProject().getId(), username);

        if (request.plannedEndDate().isBefore(request.plannedStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректные сроки работы");
        }

        if (request.plannedStartDate().isBefore(plan.getPlannedStartDate())
                || request.plannedEndDate().isAfter(plan.getPlannedEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Сроки работы выходят за границы плана");
        }

        Task task = new Task(
                plan,
                request.name(),
                request.description(),
                request.plannedStartDate(),
                request.plannedEndDate(),
                request.plannedCost()
        );

        Task savedTask = taskRepository.saveAndFlush(task);

        BigDecimal totalCost = taskRepository.sumPlannedCostByPlanId(planId);

        plan.setBudget(totalCost != null ? totalCost : BigDecimal.ZERO);

        return toResponse(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(Long planId, String username) {
        ProjectPlan plan = planRepository.findById(planId)
                .orElseThrow(this::planNotFound);

        accessService.requireViewAccess(plan.getProject().getId(), username);

        return taskRepository.findByPlanId(planId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ResponseStatusException planNotFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "План проекта не найден");
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getPlan().getId(),
                task.getName(),
                task.getDescription(),
                task.getPlannedStartDate(),
                task.getPlannedEndDate(),
                task.getPlannedCost()
        );
    }
}
