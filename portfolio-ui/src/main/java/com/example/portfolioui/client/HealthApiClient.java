package com.example.portfolioui.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HealthApiClient {

    private final RestClient restClient;

    public HealthApiClient(RestClient portfolioRestClient) {
        this.restClient = portfolioRestClient;
    }

    public HealthResponse getHealth() {
        return restClient.get()
                .uri("/api/health")
                .retrieve()
                .body(HealthResponse.class);
    }

    public record HealthResponse(
            String status,
            String application
    ) {
    }
}
