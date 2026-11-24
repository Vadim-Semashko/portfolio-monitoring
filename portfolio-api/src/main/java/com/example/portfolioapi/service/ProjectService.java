package com.example.portfolioapi.service;

import com.example.portfolioapi.dto.project.CreateProjectRequest;
import com.example.portfolioapi.dto.project.ProjectResponse;
import com.example.portfolioapi.entity.Portfolio;
import com.example.portfolioapi.entity.Project;
import com.example.portfolioapi.entity.User;
import com.example.portfolioapi.repository.PortfolioRepository;
import com.example.portfolioapi.repository.ProjectRepository;
import com.example.portfolioapi.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProjectService {

    private static final String ADMIN = "ADMIN";
    private static final String PORTFOLIO_MANAGER = "PORTFOLIO_MANAGER";
    private static final String PROJECT_MANAGER = "PROJECT_MANAGER";

    private final ProjectRepository projectRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, PortfolioRepository portfolioRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, String username) {
        User currentUser = getActiveUser(username);

        if (!hasRole(currentUser, PORTFOLIO_MANAGER)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Создавать проекты может только руководитель портфеля");
        }

        Portfolio portfolio = portfolioRepository
                .findById(request.portfolioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Портфель не найден"));

        if (!portfolio.getManager().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Портфель не найден");
        }

        User manager = userRepository.findById(request.managerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Руководитель проекта не найден"));

        if (!manager.isActive() || !hasRole(manager, PROJECT_MANAGER)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Выбранный пользователь не может руководить проектом");
        }

        if (request.endDate().isBefore(request.startDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата завершения не может быть раньше даты начала");
        }

        Project project = new Project(
                portfolio,
                manager,
                request.name(),
                request.description(),
                request.startDate(),
                request.endDate()
        );

        return toResponse(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjects(String username) {

        User user = getActiveUser(username);
        String role = user.getRole().getName();

        List<Project> projects;

        switch (role) {
            case ADMIN -> projects = projectRepository.findAllBy();
            case PORTFOLIO_MANAGER -> projects = projectRepository.findByPortfolioManagerId(user.getId());
            case PROJECT_MANAGER -> projects = projectRepository.findByManagerId(user.getId());
            default -> throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Недостаточно прав для просмотра проектов");
        }

        return projects.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(Long projectId, String username) {
        User user = getActiveUser(username);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Проект не найден"));

        if (!canViewProject(user, project)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Проект не найден");
        }

        return toResponse(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjectsByPortfolio(Long portfolioId, String username) {
        User user = getActiveUser(username);

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Портфель не найден"));

        List<Project> projects = projectRepository.findByPortfolioId(portfolioId);

        String role = user.getRole().getName();

        if (ADMIN.equals(role) || (PORTFOLIO_MANAGER.equals(role)
                && portfolio.getManager().getId().equals(user.getId()))) {
            return projects.stream()
                    .map(this::toResponse)
                    .toList();
        }

        if (PROJECT_MANAGER.equals(role)) {
            List<ProjectResponse> visibleProjects = projects.stream()
                    .filter(project -> project.getManager().getId().equals(user.getId()))
                    .map(this::toResponse)
                    .toList();

            if (!visibleProjects.isEmpty()) {
                return visibleProjects;
            }
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Портфель не найден");
    }

    private boolean canViewProject(User user, Project project) {
        String role = user.getRole().getName();

        if (ADMIN.equals(role)) {
            return true;
        }

        if (PORTFOLIO_MANAGER.equals(role)) {
            return project.getPortfolio().getManager().getId().equals(user.getId());
        }

        if (PROJECT_MANAGER.equals(role)) {
            return project.getManager().getId().equals(user.getId());
        }

        return false;
    }

    private boolean hasRole(User user, String role) {
        return role.equals(user.getRole().getName());
    }

    private User getActiveUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не найден"));

        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Учётная запись неактивна");
        }

        return user;
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getPortfolio().getId(),
                project.getPortfolio().getName(),
                project.getManager().getId(),
                project.getManager().getFullName(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getStatus()
        );
    }
}
