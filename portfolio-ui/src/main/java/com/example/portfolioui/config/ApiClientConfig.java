package com.example.portfolioui.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ApiClientConfig {

    @Bean
    public RestClient portfolioRestClient(RestClient.Builder builder, @Value("${portfolio.api.base-url}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }
}
