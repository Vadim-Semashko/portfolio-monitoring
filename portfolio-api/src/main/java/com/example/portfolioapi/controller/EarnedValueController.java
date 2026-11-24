package com.example.portfolioapi.controller;

import com.example.portfolioapi.dto.evm.EarnedValueResponse;
import com.example.portfolioapi.service.EarnedValueService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/evm")
public class EarnedValueController {

    private final EarnedValueService earnedValueService;

    public EarnedValueController(EarnedValueService earnedValueService) {
        this.earnedValueService = earnedValueService;
    }

    @PostMapping("/calculate")
    @ResponseStatus(HttpStatus.OK)
    public EarnedValueResponse calculate(
            @PathVariable Long projectId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate reportDate,

            Authentication authentication
    ) {
        return earnedValueService.calculate(projectId, reportDate, authentication.getName());
    }

    @GetMapping("/history")
    public List<EarnedValueResponse> getHistory(@PathVariable Long projectId, Authentication authentication) {
        return earnedValueService.getHistory(projectId, authentication.getName());
    }
}
