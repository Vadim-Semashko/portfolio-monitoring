package com.example.portfolioui;

import com.example.portfolioui.client.AuthApiClient;
import com.example.portfolioui.security.ApiUserPrincipal;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.spring.security.AuthenticationContext;

import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.GrantedAuthority;

@Route("")
@PageTitle("Главная")
@PermitAll
public class MainView extends VerticalLayout {

    public MainView(AuthenticationContext authenticationContext, AuthApiClient authApiClient) {
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("Мониторинг портфеля проектов");

        ApiUserPrincipal user = authenticationContext.getAuthenticatedUser(ApiUserPrincipal.class).orElseThrow();

        Paragraph welcome = new Paragraph("Вы вошли как: " + user.username());

        Paragraph roles = new Paragraph("Роли: " + user.authorities().stream().map(GrantedAuthority::getAuthority).toList());

        Paragraph apiStatus = new Paragraph();

        Button checkApi = new Button(
                "Проверить авторизацию API",
                event -> {
                    try {
                        var response = authApiClient.getCurrentUser(user.accessToken());

                        apiStatus.setText("API подтвердил пользователя: " + response.username());

                    } catch (Exception exception) {
                        apiStatus.setText("Не удалось подтвердить авторизацию API");
                    }
                }
        );

        Button logout = new Button("Выйти", event -> authenticationContext.logout());

        add(
                title,
                welcome,
                roles,
                checkApi,
                apiStatus,
                logout
        );
    }
}
