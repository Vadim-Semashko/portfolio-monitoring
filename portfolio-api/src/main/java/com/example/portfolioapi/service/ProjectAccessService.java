package com.example.portfolioapi.service;

import com.example.portfolioapi.entity.Project;
import com.example.portfolioapi.entity.User;
import com.example.portfolioapi.repository.ProjectRepository;
import com.example.portfolioapi.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProjectAccessService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectAccessService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Project requireViewAccess(Long projectId, String username) {
        User user = getCurrentUser(username);
        Project project = getProject(projectId);

        String role = user.getRole().getName();

        boolean allowed = role.equals("ADMIN") || isPortfolioManager(user, project) || isProjectManager(user, project);

        if (!allowed) {
            throw notFound();
        }

        return project;
    }

    @Transactional(readOnly = true)
    public Project requireManageAccess(Long projectId, String username) {
        User user = getCurrentUser(username);
        Project project = getProject(projectId);

        boolean allowed = isPortfolioManager(user, project) || isProjectManager(user, project);

        if (!allowed) {
            throw notFound();
        }

        return project;
    }

    private boolean isPortfolioManager(User user, Project project) {
        return "PORTFOLIO_MANAGER".equals(user.getRole().getName()) && project.getPortfolio().getManager().getId().equals(user.getId());
    }

    private boolean isProjectManager(User user, Project project) {
        return "PROJECT_MANAGER".equals(user.getRole().getName()) && project.getManager().getId().equals(user.getId());
    }

    private User getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(this::notFound);

        if (!user.isActive()) {
            throw notFound();
        }

        return user;
    }

    private Project getProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(this::notFound);
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Проект не найден");
    }
}
