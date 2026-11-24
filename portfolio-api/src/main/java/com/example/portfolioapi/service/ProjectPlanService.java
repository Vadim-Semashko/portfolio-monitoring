package com.example.portfolioapi.service;

import com.example.portfolioapi.dto.plan.CreateProjectPlanRequest;
import com.example.portfolioapi.dto.plan.ProjectPlanResponse;
import com.example.portfolioapi.entity.Project;
import com.example.portfolioapi.entity.ProjectPlan;
import com.example.portfolioapi.repository.ProjectPlanRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProjectPlanService {

    private final ProjectPlanRepository planRepository;
    private final ProjectAccessService accessService;

    public ProjectPlanService(ProjectPlanRepository planRepository, ProjectAccessService accessService) {
        this.planRepository = planRepository;
        this.accessService = accessService;
    }

    @Transactional
    public ProjectPlanResponse createPlan(Long projectId, CreateProjectPlanRequest request, String username) {
        Project project = accessService.requireManageAccess(projectId, username);

        if (planRepository.existsByProjectId(projectId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "План проекта уже существует");
        }

        if (request.plannedEndDate().isBefore(request.plannedStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректные даты плана");
        }

        if (request.plannedStartDate().isBefore(project.getStartDate()) || request.plannedEndDate().isAfter(project.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Сроки плана выходят за границы проекта");
        }

        ProjectPlan plan = new ProjectPlan(
                project,
                request.plannedStartDate(),
                request.plannedEndDate()
        );

        return toResponse(planRepository.save(plan));
    }

    @Transactional(readOnly = true)
    public ProjectPlanResponse getPlan(Long projectId, String username) {
        accessService.requireViewAccess(projectId, username);

        ProjectPlan plan = planRepository
                .findByProjectId(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "План проекта не найден"));

        return toResponse(plan);
    }

    private ProjectPlanResponse toResponse(ProjectPlan plan) {
        return new ProjectPlanResponse(
                plan.getId(),
                plan.getProject().getId(),
                plan.getBudget(),
                plan.getPlannedStartDate(),
                plan.getPlannedEndDate()
        );
    }
}
