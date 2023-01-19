/*-
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package dev.hilla.sso.endpoint;

import java.util.List;

import dev.hilla.Nonnull;

/**
 * A convenience class that contains all the information about the current SSO
 * session.
 */
public class SingleSignOnData {
    private User user;
    private String logoutUrl;
    private @Nonnull List<@Nonnull String> registeredProviders = List.of();
    private boolean backChannelLogoutEnabled;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    @Nonnull
    public List<@Nonnull String> getRegisteredProviders() {
        return registeredProviders;
    }

    public void setRegisteredProviders(
            @Nonnull List<@Nonnull String> registeredProviders) {
        this.registeredProviders = registeredProviders;
    }

    public boolean isBackChannelLogoutEnabled() {
        return backChannelLogoutEnabled;
    }

    public void setBackChannelLogoutEnabled(boolean backChannelLogoutEnabled) {
        this.backChannelLogoutEnabled = backChannelLogoutEnabled;
    }
}
