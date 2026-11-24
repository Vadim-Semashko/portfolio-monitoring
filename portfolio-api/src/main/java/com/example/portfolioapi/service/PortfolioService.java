package com.example.portfolioapi.service;

import com.example.portfolioapi.dto.portfolio.CreatePortfolioRequest;
import com.example.portfolioapi.dto.portfolio.PortfolioResponse;
import com.example.portfolioapi.entity.Portfolio;
import com.example.portfolioapi.entity.User;
import com.example.portfolioapi.repository.PortfolioRepository;
import com.example.portfolioapi.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PortfolioService {

    private static final String ADMIN = "ADMIN";
    private static final String PORTFOLIO_MANAGER = "PORTFOLIO_MANAGER";

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    public PortfolioService(PortfolioRepository portfolioRepository, UserRepository userRepository) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PortfolioResponse createPortfolio(CreatePortfolioRequest request, String username) {
        User user = getActiveUser(username);

        if (!PORTFOLIO_MANAGER.equals(user.getRole().getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Недостаточно прав для создания портфеля");
        }

        Portfolio portfolio = new Portfolio(
                user,
                request.name(),
                request.description()
        );

        Portfolio saved = portfolioRepository.save(portfolio);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PortfolioResponse> getPortfolios(String username) {
        User user = getActiveUser(username);

        List<Portfolio> portfolios;

        if (ADMIN.equals(user.getRole().getName())) {
            portfolios = portfolioRepository.findAllBy();
        } else if (PORTFOLIO_MANAGER.equals(user.getRole().getName())) {
            portfolios = portfolioRepository.findByManagerId(user.getId());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Недостаточно прав для просмотра портфелей");
        }

        return portfolios.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PortfolioResponse getPortfolio(Long portfolioId, String username) {
        User user = getActiveUser(username);

        Portfolio portfolio = portfolioRepository
                .findById(portfolioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Портфель не найден"));

        boolean isAdmin = ADMIN.equals(user.getRole().getName());

        boolean isOwner = portfolio.getManager().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Портфель не найден");
        }

        return toResponse(portfolio);
    }

    private User getActiveUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не найден"));

        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Учётная запись неактивна");
        }

        return user;
    }

    private PortfolioResponse toResponse(Portfolio portfolio) {
        return new PortfolioResponse(
                portfolio.getId(),
                portfolio.getName(),
                portfolio.getDescription(),
                portfolio.getManager().getId(),
                portfolio.getManager().getFullName()
        );
    }
}
