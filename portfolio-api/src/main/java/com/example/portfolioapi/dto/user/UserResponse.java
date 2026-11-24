package com.example.portfolioapi.dto.user;

public record UserResponse(
        Long id,
        String username,
        String fullName,
        String email,
        String role,
        boolean active
) {
}
