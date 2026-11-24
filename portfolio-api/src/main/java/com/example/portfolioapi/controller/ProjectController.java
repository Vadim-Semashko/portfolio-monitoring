package com.example.portfolioapi.controller;

import com.example.portfolioapi.dto.project.CreateProjectRequest;
import com.example.portfolioapi.dto.project.ProjectResponse;
import com.example.portfolioapi.service.ProjectService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(@Valid @RequestBody CreateProjectRequest request, Authentication authentication) {
        return projectService.createProject(request, authentication.getName());
    }

    @GetMapping
    public List<ProjectResponse> getProjects(Authentication authentication) {
        return projectService.getProjects(authentication.getName());
    }

    @GetMapping("/{id}")
    public ProjectResponse getProject(@PathVariable Long id, Authentication authentication) {
        return projectService.getProject(id, authentication.getName());
    }
}
