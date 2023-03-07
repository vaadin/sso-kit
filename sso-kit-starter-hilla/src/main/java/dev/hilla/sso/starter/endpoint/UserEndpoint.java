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

import java.util.Optional;

import com.vaadin.flow.server.auth.AnonymousAllowed;

import dev.hilla.Endpoint;
import dev.hilla.sso.starter.SingleSignOnContext;

/**
 * Endpoint for getting information about the current user.
 */
@Endpoint
@AnonymousAllowed
public class UserEndpoint {

    /**
     * Returns the current user, if any.
     *
     * @return an instance of {@link User} that contains the user's information,
     *         mapped from the current OIDC user.
     */
    public Optional<User> getAuthenticatedUser() {
        return SingleSignOnContext.getOidcUser().map(User::from);
    }
}
