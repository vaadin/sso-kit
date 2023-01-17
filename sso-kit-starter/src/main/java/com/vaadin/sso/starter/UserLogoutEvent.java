package com.vaadin.sso.starter;

import org.springframework.context.ApplicationEvent;

public class UserLogoutEvent extends ApplicationEvent {

    public UserLogoutEvent(Object principal) {
        super(principal);
    }
}
