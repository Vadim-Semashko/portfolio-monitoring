package com.example.portfolioapi.config;

import com.example.portfolioapi.entity.Role;
import com.example.portfolioapi.entity.User;
import com.example.portfolioapi.repository.RoleRepository;
import com.example.portfolioapi.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    public CommandLineRunner initializeAdmin(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,

            @Value("${app.admin.username:}")
            String username,

            @Value("${app.admin.password:}")
            String password,

            @Value("${app.admin.email:}")
            String email
    ) {
        return args -> {

            if (username.isBlank() && password.isBlank() && email.isBlank()) {
                return;
            }

            if (username.isBlank() || password.isBlank() || email.isBlank()) {
                throw new IllegalStateException("Не все параметры администратора указаны");
            }

            if (password.length() < 6) {
                throw new IllegalStateException("Пароль администратора должен содержать не менее 6 символов");
            }

            if (userRepository.existsByUsername(username)) {
                return;
            }

            if (userRepository.existsByEmail(email)) {
                throw new IllegalStateException("Email администратора уже используется");
            }

            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new IllegalStateException("Роль ADMIN отсутствует"));

            User admin = new User(
                    adminRole,
                    username,
                    passwordEncoder.encode(password),
                    "Администратор системы",
                    email
            );

            userRepository.save(admin);

            System.out.println("Начальная учётная запись администратора создана");
        };
    }
}
