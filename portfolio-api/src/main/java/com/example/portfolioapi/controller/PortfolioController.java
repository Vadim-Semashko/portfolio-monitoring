package com.example.portfolioapi.controller;

import com.example.portfolioapi.dto.portfolio.CreatePortfolioRequest;
import com.example.portfolioapi.dto.portfolio.PortfolioResponse;
import com.example.portfolioapi.dto.project.ProjectResponse;
import com.example.portfolioapi.service.PortfolioService;

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
@RequestMapping("/api/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final ProjectService projectService;

    public PortfolioController(PortfolioService portfolioService, ProjectService projectService) {
        this.portfolioService = portfolioService;
        this.projectService = projectService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PortfolioResponse createPortfolio(@Valid @RequestBody CreatePortfolioRequest request, Authentication authentication) {
        return portfolioService.createPortfolio(request, authentication.getName());
    }

    @GetMapping
    public List<PortfolioResponse> getPortfolios(Authentication authentication) {
        return portfolioService.getPortfolios(authentication.getName());
    }

    @GetMapping("/{id}")
    public PortfolioResponse getPortfolio(@PathVariable Long id, Authentication authentication) {
        return portfolioService.getPortfolio(id, authentication.getName());
    }

    @GetMapping("/{id}/projects")
    public List<ProjectResponse> getPortfolioProjects(@PathVariable Long id, Authentication authentication) {
        return projectService.getProjectsByPortfolio(id, authentication.getName());
    }
}
