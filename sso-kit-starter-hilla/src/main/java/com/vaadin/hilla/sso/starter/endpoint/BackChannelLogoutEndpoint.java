/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */
package com.vaadin.hilla.sso.starter.endpoint;

import jakarta.annotation.security.PermitAll;

import reactor.core.publisher.Flux;

import com.vaadin.hilla.Endpoint;
import com.vaadin.hilla.Nonnull;
import com.vaadin.hilla.sso.starter.BackChannelLogoutSubscription;
import com.vaadin.hilla.sso.starter.SingleSignOnContext;

/**
 * Endpoint for getting messages about back-channel logout events.
 */
@Endpoint
public class BackChannelLogoutEndpoint {

    private final SingleSignOnContext singleSignOnContext;

    public BackChannelLogoutEndpoint(SingleSignOnContext singleSignOnContext) {
        this.singleSignOnContext = singleSignOnContext;
    }

    /**
     * Returns a subscription to back-channel logout events.
     *
     * @return a cancellable subscription to back-channel logout events.
     */
    @PermitAll
    @Nonnull
    public Flux<BackChannelLogoutSubscription.@Nonnull Message> subscribe() {
        return singleSignOnContext.getBackChannelLogoutFlux();
    }
}
