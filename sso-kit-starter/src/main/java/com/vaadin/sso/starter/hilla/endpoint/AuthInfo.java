/*-
 * Copyright (C) 2022-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.sso.starter.hilla.endpoint;

import java.util.List;
import java.util.Optional;

import dev.hilla.Nonnull;

public class AuthInfo {
    private Optional<User> user = Optional.empty();
    private Optional<String> logoutUrl = Optional.empty();
    private @Nonnull List<@Nonnull String> registeredProviders = List.of();
    private boolean backChannelLogoutEnabled;

    public Optional<User> getUser() {
        return user;
    }

    public void setUser(Optional<User> user) {
        this.user = user;
    }

    public Optional<String> getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(Optional<String> logoutUrl) {
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
