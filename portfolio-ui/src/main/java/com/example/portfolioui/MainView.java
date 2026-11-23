package com.example.portfolioui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class MainView extends VerticalLayout {

    public MainView() {
        H1 title = new H1("Мониторинг портфеля проектов");

        Paragraph description = new Paragraph(
                "Система мониторинга и прогнозирования реализации портфеля проектов предприятия."
        );

        add(title, description);
    }
}
