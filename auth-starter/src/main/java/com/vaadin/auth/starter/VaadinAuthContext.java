package com.vaadin.auth.starter;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Optional;

/**
 * An interface to access the authentication context of the application
 *
 * @author Vaadin Ltd
 * @since 1.0
 */
public interface VaadinAuthContext {
    /**
     * Gets the authenticated user on the application done by the SSO providers
     *
     * @return an optional {@link OidcUser} object if the user is authenticated
     *         or an optional {@code null} otherwise
     */
    Optional<OidcUser> getAuthenticatedUser();
}
