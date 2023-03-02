/*-
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package dev.hilla.sso.starter.endpoint;

import dev.hilla.Endpoint;
import dev.hilla.Nonnull;
import dev.hilla.sso.starter.BackChannelLogoutSubscription;
import dev.hilla.sso.starter.SingleSignOnContext;
import jakarta.annotation.security.PermitAll;
import reactor.core.publisher.Flux;

/**
 * Endpoint for getting messages about back-channel logout events.
 */
@Endpoint
public class BackChannelLogoutEndpoint {

    private final SingleSignOnContext context;

    public BackChannelLogoutEndpoint(SingleSignOnContext context) {
        this.context = context;
    }

    /**
     * Returns a subscription to back-channel logout events.
     *
     * @return a cancellable subscription to back-channel logout events.
     */
    @PermitAll
    @Nonnull
    public Flux<BackChannelLogoutSubscription.@Nonnull Message> subscribe() {
        return context.getBackChannelLogoutFlux();
    }
}
