package com.example.portfolioapi.config;

import com.example.portfolioapi.security.DatabaseJwtAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, DatabaseJwtAuthenticationConverter jwtConverter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/health").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/auth/admin-check").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/portfolios/*/projects").hasAnyRole("ADMIN", "PORTFOLIO_MANAGER", "PROJECT_MANAGER")
                        .requestMatchers("/api/portfolios/**").hasAnyRole("ADMIN", "PORTFOLIO_MANAGER")
                        .requestMatchers("/api/projects/**").hasAnyRole("ADMIN", "PORTFOLIO_MANAGER", "PROJECT_MANAGER")
                        .requestMatchers("/api/plans/**").hasAnyRole("ADMIN", "PORTFOLIO_MANAGER", "PROJECT_MANAGER")
                        .requestMatchers("/api/tasks/**").hasAnyRole("ADMIN", "PORTFOLIO_MANAGER", "PROJECT_MANAGER")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter))
                )
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
