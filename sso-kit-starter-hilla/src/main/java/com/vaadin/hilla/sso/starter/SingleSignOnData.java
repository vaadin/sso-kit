/*-
 * Copyright (C) 2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.hilla.sso.starter;

import java.util.List;

import com.vaadin.hilla.Nonnull;

/**
 * A convenience class that contains all the information about the current SSO
 * session.
 */
public class SingleSignOnData {

    private boolean authenticated;
    private @Nonnull List<@Nonnull String> roles = List.of();
    private @Nonnull String loginLink = "";
    private String logoutLink;
    private boolean backChannelLogoutEnabled;

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Nonnull
    public List<@Nonnull String> getRoles() {
        return roles;
    }

    public void setRoles(@Nonnull List<@Nonnull String> roles) {
        this.roles = roles;
    }

    @Nonnull
    public String getLoginLink() {
        return loginLink;
    }

    public void setLoginLink(@Nonnull String loginLink) {
        this.loginLink = loginLink;
    }

    public String getLogoutLink() {
        return logoutLink;
    }

    public void setLogoutLink(String logoutLink) {
        this.logoutLink = logoutLink;
    }

    public boolean isBackChannelLogoutEnabled() {
        return backChannelLogoutEnabled;
    }

    public void setBackChannelLogoutEnabled(boolean backChannelLogoutEnabled) {
        this.backChannelLogoutEnabled = backChannelLogoutEnabled;
    }
}
