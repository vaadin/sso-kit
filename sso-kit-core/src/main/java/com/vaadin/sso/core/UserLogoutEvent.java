/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */
package com.vaadin.sso.core;

import org.springframework.context.ApplicationEvent;

/**
 * An event signalling that a user (principal) has been logged out.
 */
public class UserLogoutEvent extends ApplicationEvent {

    public UserLogoutEvent(Object principal) {
        super(principal);
    }
}
