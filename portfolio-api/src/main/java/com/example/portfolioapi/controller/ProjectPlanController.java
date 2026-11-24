package com.example.portfolioapi.controller;

import com.example.portfolioapi.dto.plan.CreateProjectPlanRequest;
import com.example.portfolioapi.dto.plan.ProjectPlanResponse;
import com.example.portfolioapi.service.ProjectPlanService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/plan")
public class ProjectPlanController {

    private final ProjectPlanService planService;

    public ProjectPlanController(ProjectPlanService planService) {
        this.planService = planService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectPlanResponse createPlan(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateProjectPlanRequest request,
            Authentication authentication) {
        return planService.createPlan(projectId, request, authentication.getName());
    }

    @GetMapping
    public ProjectPlanResponse getPlan(@PathVariable Long projectId, Authentication authentication) {
        return planService.getPlan(projectId, authentication.getName());
    }
}
