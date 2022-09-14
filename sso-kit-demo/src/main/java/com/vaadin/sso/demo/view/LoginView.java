package com.vaadin.sso.demo.view;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    private static final String OAUTH_URL = "/oauth2/authorization/";

    public LoginView() {
        setAlignItems(FlexComponent.Alignment.CENTER);
        getStyle().set("padding", "200px");
        add(loginLink("keycloak", "Keycloak"));
    }

    private Anchor loginLink(String id, String name) {
        final var link = new Anchor(OAUTH_URL + id, "Login with " + name);
        link.getElement().setAttribute("router-ignore", true);
        return link;
    }
}