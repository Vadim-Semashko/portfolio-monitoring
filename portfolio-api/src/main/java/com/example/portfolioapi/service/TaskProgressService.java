package com.example.portfolioapi.service;

import com.example.portfolioapi.dto.progress.SaveTaskProgressRequest;
import com.example.portfolioapi.dto.progress.TaskProgressResponse;
import com.example.portfolioapi.entity.Task;
import com.example.portfolioapi.entity.TaskProgress;
import com.example.portfolioapi.repository.TaskProgressRepository;
import com.example.portfolioapi.repository.TaskRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskProgressService {

    private final TaskProgressRepository progressRepository;
    private final TaskRepository taskRepository;
    private final ProjectAccessService accessService;

    public TaskProgressService(TaskProgressRepository progressRepository, TaskRepository taskRepository, ProjectAccessService accessService) {
        this.progressRepository = progressRepository;
        this.taskRepository = taskRepository;
        this.accessService = accessService;
    }

    @Transactional
    public TaskProgressResponse saveProgress(Long taskId, SaveTaskProgressRequest request, String username) {
        Task task = getTask(taskId);

        accessService.requireManageAccess(task.getPlan().getProject().getId(), username);

        validateDate(request.reportDate(), task.getPlan().getProject().getStartDate());

        TaskProgress progress = progressRepository.findByTaskIdAndReportDate(taskId, request.reportDate())
                .orElseGet(() -> new TaskProgress(task, request.reportDate(), request.completionPercent()));

        progress.setCompletionPercent(request.completionPercent());

        TaskProgress saved = progressRepository.save(progress);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TaskProgressResponse> getProgressHistory(Long taskId, String username) {
        Task task = getTask(taskId);

        accessService.requireViewAccess(task.getPlan().getProject().getId(), username);

        return progressRepository.findByTaskIdOrderByReportDateAsc(taskId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Task getTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Работа не найдена"));
    }

    private void validateDate(LocalDate reportDate, LocalDate projectStartDate) {
        if (reportDate.isBefore(projectStartDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Отчётная дата раньше начала проекта");
        }

        if (reportDate.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя регистрировать выполнение на будущую дату");
        }
    }

    private TaskProgressResponse toResponse(TaskProgress progress) {
        return new TaskProgressResponse(
                progress.getId(),
                progress.getTask().getId(),
                progress.getReportDate(),
                progress.getCompletionPercent()
        );
    }
}
