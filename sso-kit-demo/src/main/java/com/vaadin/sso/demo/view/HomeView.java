package com.vaadin.sso.demo.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Home")
@Route(layout = MainLayout.class, value = "")
public class HomeView extends VerticalLayout {

    public HomeView() {
        setSizeFull();
        setSpacing(false);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

        add(new H2("This page does not require authentication"));

        add(new Anchor("/profile", "Navigate to the secured Profile page"));

        add(new Button("Navigate to the secured Profile page",
                e -> UI.getCurrent().navigate("profile")));
    }
}
