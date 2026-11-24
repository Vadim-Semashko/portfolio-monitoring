
package com.example.portfolioapi.service;

import com.example.portfolioapi.dto.user.CreateUserRequest;
import com.example.portfolioapi.dto.user.UserResponse;
import com.example.portfolioapi.entity.Role;
import com.example.portfolioapi.entity.User;
import com.example.portfolioapi.repository.RoleRepository;
import com.example.portfolioapi.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private static final Set<String> ALLOWED_ROLES = Set.of("ADMIN", "PORTFOLIO_MANAGER", "PROJECT_MANAGER");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь с таким логином уже существует");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь с таким email уже существует");
        }

        if (!ALLOWED_ROLES.contains(request.role())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недопустимая роль пользователя");
        }

        Role role = roleRepository.findByName(request.role())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Роль не найдена"));

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User(
                role,
                request.username(),
                passwordHash,
                request.fullName(),
                request.email()
        );

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse deactivateUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        if ("ADMIN".equals(user.getRole().getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Деактивация администратора запрещена");
        }

        user.setActive(false);

        return toResponse(user);
    }

    private UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getName(),
                user.isActive()
        );
    }
}
