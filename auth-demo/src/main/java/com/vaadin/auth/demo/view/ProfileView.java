package com.vaadin.auth.demo.view;

import javax.annotation.security.PermitAll;

import com.vaadin.auth.starter.VaadinAuthContext;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PermitAll
@PageTitle("Profile")
@Route(layout = MainLayout.class, value = "profile")
public class ProfileView extends VerticalLayout {

    private final Avatar avatar = new Avatar();

    private final TextField nameField = new TextField("Name");

    private final EmailField emailField = new EmailField("E-mail");

    public ProfileView(VaadinAuthContext authContext) {

        authContext.getAuthenticatedUser().ifPresent(user -> {
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
        });

        add(new Button("Logout", click -> authContext.logout()));
    }
}
