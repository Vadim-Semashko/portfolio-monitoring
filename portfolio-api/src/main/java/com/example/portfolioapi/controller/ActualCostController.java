package com.example.portfolioapi.controller;

import com.example.portfolioapi.dto.cost.ActualCostResponse;
import com.example.portfolioapi.dto.cost.CreateActualCostRequest;
import com.example.portfolioapi.service.ActualCostService;

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
@RequestMapping("/api/tasks/{taskId}/costs")
public class ActualCostController {

    private final ActualCostService costService;

    public ActualCostController(ActualCostService costService) {
        this.costService = costService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActualCostResponse createCost(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateActualCostRequest request,
            Authentication authentication
    ) {
        return costService.createCost(taskId, request, authentication.getName());
    }

    @GetMapping
    public List<ActualCostResponse> getHistory(@PathVariable Long taskId, Authentication authentication) {
        return costService.getCostHistory(taskId, authentication.getName());
    }
}
