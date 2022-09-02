package com.vaadin.auth.demo.view;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.auth.starter.VaadinAuthContext;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServletRequest;

@PermitAll
@PageTitle("Profile")
@Route(layout = MainLayout.class, value = "profile")
public class ProfileView extends VerticalLayout {

    private static final String LOGOUT_SUCCESS_URL = "/";

    private final Avatar avatar = new Avatar();

    private final TextField nameField = new TextField("Name");

    private final EmailField emailField = new EmailField("E-mail");

    public ProfileView(VaadinAuthContext context) {
        var optionalUser = context.getAuthenticatedUser();

        avatar.setWidth("96px");
        avatar.setHeight("96px");
        nameField.setWidthFull();
        emailField.setWidthFull();
        nameField.setReadOnly(true);
        emailField.setReadOnly(true);

        setSpacing(false);

        optionalUser.ifPresent(user -> {
            String fullName = user.getFullName();
            String email = user.getEmail();
            String picture = user.getPicture();

            avatar.setName(fullName);
            avatar.setImage(picture);
            nameField.setValue(fullName);
            emailField.setValue(email);
        });

        add(avatar, nameField, emailField);

        add(new Button("Logout", click -> {
            UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            HttpServletRequest request = VaadinServletRequest.getCurrent()
                    .getHttpServletRequest();
            logoutHandler.logout(request, null, null);
        }));
    }
}
