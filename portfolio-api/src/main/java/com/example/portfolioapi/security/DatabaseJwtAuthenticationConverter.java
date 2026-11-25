package com.example.portfolioapi.security;

import com.example.portfolioapi.entity.User;
import com.example.portfolioapi.repository.UserRepository;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DatabaseJwtAuthenticationConverter implements Converter<Jwt, JwtAuthenticationToken> {

    private final UserRepository userRepository;

    public DatabaseJwtAuthenticationConverter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public JwtAuthenticationToken convert(Jwt jwt) {

        User user = userRepository.findByUsername(jwt.getSubject())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        if (!user.isActive()) {
            throw new DisabledException("Учётная запись неактивна");
        }

        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

        return new JwtAuthenticationToken(jwt, authorities, user.getUsername());
    }
}
