package com.example.portfolioui.security;

import com.example.portfolioui.client.AuthApiClient;
import com.example.portfolioui.dto.auth.CurrentUserResponse;
import com.example.portfolioui.dto.auth.TokenResponse;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class ApiAuthenticationProvider implements AuthenticationProvider {

    private final AuthApiClient authApiClient;

    public ApiAuthenticationProvider(AuthApiClient authApiClient) {
        this.authApiClient = authApiClient;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            TokenResponse tokenResponse = authApiClient.login(username, password);

            if (tokenResponse == null || tokenResponse.accessToken() == null || tokenResponse.accessToken().isBlank()) {
                throw new AuthenticationServiceException("API не вернул токен");
            }

            CurrentUserResponse userResponse = authApiClient.getCurrentUser(tokenResponse.accessToken());

            if (userResponse == null || userResponse.roles() == null || !username.equals(userResponse.username())) {
                throw new AuthenticationServiceException("API вернул некорректные данные пользователя");
            }

            List<SimpleGrantedAuthority> authorities =
                    userResponse.roles().stream()
                            .filter(role -> List.of("ADMIN", "PORTFOLIO_MANAGER", "PROJECT_MANAGER").contains(role))
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .toList();

            if (authorities.isEmpty()) {
                throw new BadCredentialsException("Отсутствует разрешённая роль");
            }

            ApiUserPrincipal principal = new ApiUserPrincipal(
                    userResponse.username(),
                    tokenResponse.accessToken(),
                    List.copyOf(authorities)
            );

            return UsernamePasswordAuthenticationToken.authenticated(principal, null, authorities);

        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden exception) {

            throw new BadCredentialsException("Неверный логин или пароль");

        } catch (RestClientException exception) {

            throw new AuthenticationServiceException("Сервис авторизации недоступен", exception);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
