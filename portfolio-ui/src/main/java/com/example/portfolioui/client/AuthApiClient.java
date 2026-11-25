package com.example.portfolioui.client;

import com.example.portfolioui.dto.auth.CurrentUserResponse;
import com.example.portfolioui.dto.auth.TokenResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@Component
public class AuthApiClient {

    private final RestClient restClient;

    public AuthApiClient(RestClient portfolioRestClient) {
        this.restClient = portfolioRestClient;
    }

    public TokenResponse login(String username, String password) {
        return restClient.post()
                .uri("/api/auth/token")
                .headers(headers -> headers.setBasicAuth(username, password, StandardCharsets.UTF_8))
                .retrieve()
                .body(TokenResponse.class);
    }

    public CurrentUserResponse getCurrentUser(String accessToken) {
        return restClient.get()
                .uri("/api/auth/me")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(CurrentUserResponse.class);
    }
}
