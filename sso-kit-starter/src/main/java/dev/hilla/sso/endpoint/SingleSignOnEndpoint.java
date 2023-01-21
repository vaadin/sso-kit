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
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.server.auth.AnonymousAllowed;

import dev.hilla.Endpoint;
import dev.hilla.EndpointSubscription;
import dev.hilla.Nonnull;
import jakarta.annotation.security.PermitAll;

/**
 * Endpoint for getting information about the current user and the SSO session.
 * This is a default implementation, but a custom one can be used instead.
 * Exposed methods delegate to {@link SingleSignOnContext}.
 */
@Endpoint
public class SingleSignOnEndpoint {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SingleSignOnEndpoint.class);

    private final SingleSignOnContext context;

    public SingleSignOnEndpoint(SingleSignOnContext context) {
        this.context = context;
    }

    /**
     * Returns all data about the current SSO session, in a single object for
     * convenience. While the returned object is never null, most fields will
     * have a value only if the user is logged in.
     *
     * @return an object of type {@link SingleSignOnData} that contains all the
     *         available information.
     */
    @AnonymousAllowed
    @Nonnull
    public SingleSignOnData fetchAll() {
        return context.getSingleSignOnData();
    }

    /**
     * Returns the current user, if any.
     *
     * @return an instance of {@link User} that contains the user's information,
     *         mapped from the current OIDC user.
     */
    @AnonymousAllowed
    public Optional<User> user() {
        return SingleSignOnContext.getOidcUser().map(User::from);
    }

    /**
     * Returns the status of back-channel logout.
     *
     * @return true if back-channel logout is enabled, false otherwise.
     */
    @PermitAll
    public boolean backChannelLogoutEnabled() {
        return context.isBackChannelLogoutEnabled();
    }

    /**
     * Returns a subscription to back-channel logout events.
     *
     * @return a cancellable subscription to back-channel logout events.
     */
    @PermitAll
    @Nonnull
    public EndpointSubscription<BackChannelLogoutSubscription.@Nonnull Message> backChannelLogoutSubscription() {
        LOGGER.debug("Client subscribed to back channel logout information");

        return EndpointSubscription.of(context.getBackChannelLogoutFlux(),
                () -> {
                    LOGGER.debug(
                            "Client cancelled subscription to back channel logout information");
                });
    }

    /**
     * Returns the list of registered providers.
     *
     * @return a list of identifiers of the registered providers.
     */
    @AnonymousAllowed
    @Nonnull
    public List<@Nonnull String> registeredProviders() {
        return context.getRegisteredProviders();
    }

    /**
     * Returns the URL to be called to perform a logout on the SSO provider.
     *
     * @return the URL to call.
     */
    @PermitAll
    @Nonnull
    public String logoutUrl() {
        return context.getLogoutUrl().orElseThrow();
    }
}
