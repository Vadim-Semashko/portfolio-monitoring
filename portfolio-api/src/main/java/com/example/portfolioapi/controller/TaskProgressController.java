package com.example.portfolioapi.controller;

import com.example.portfolioapi.dto.progress.SaveTaskProgressRequest;
import com.example.portfolioapi.dto.progress.TaskProgressResponse;
import com.example.portfolioapi.service.TaskProgressService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/progress")
public class TaskProgressController {

    private final TaskProgressService progressService;

    public TaskProgressController(TaskProgressService progressService) {
        this.progressService = progressService;
    }

    @PutMapping
    public TaskProgressResponse saveProgress(
            @PathVariable Long taskId,
            @Valid @RequestBody SaveTaskProgressRequest request,
            Authentication authentication
    ) {
        return progressService.saveProgress(taskId, request, authentication.getName());
    }

    @GetMapping
    public List<TaskProgressResponse> getHistory(@PathVariable Long taskId, Authentication authentication) {
        return progressService.getProgressHistory(taskId, authentication.getName());
    }
}
