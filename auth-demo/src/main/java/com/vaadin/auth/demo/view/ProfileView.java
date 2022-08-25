package com.vaadin.auth.demo.view;

import javax.annotation.security.PermitAll;

import com.vaadin.flow.component.html.Anchor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PermitAll
@PageTitle("Profile")
@Route(layout = MainLayout.class, value = "profile")
public class ProfileView extends VerticalLayout {

    private static final String LOGOUT = "/logout";

    private final Avatar avatar = new Avatar();

    private final TextField nameField = new TextField("Name");

    private final EmailField emailField = new EmailField("E-mail");

    public ProfileView() {
        OidcUser user = (OidcUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        String fullName = user.getFullName();
        String email = user.getEmail();
        String picture = user.getPicture();

        setSpacing(false);

        avatar.setWidth("96px");
        avatar.setHeight("96px");
        nameField.setWidthFull();
        emailField.setWidthFull();
        nameField.setReadOnly(true);
        emailField.setReadOnly(true);

        avatar.setName(fullName);
        avatar.setImage(picture);
        nameField.setValue(fullName);
        emailField.setValue(email);

        add(avatar, nameField, emailField);

        add(logoutLink());
    }

    private Anchor logoutLink() {
        final var link = new Anchor(LOGOUT, "Logout");
        link.getElement().setAttribute("router-ignore", true);
        return link;
    }
}
