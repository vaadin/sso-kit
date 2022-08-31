package com.vaadin.auth.starter;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Optional;

/**
 * Default implementation of the {@link VaadinAuthContext} interface.
 */

public class VaadinAuthContextImpl implements VaadinAuthContext {

    @Override
    public Optional<OidcUser> getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null) {
            return Optional.empty();
        }

        if (!(authentication.getPrincipal() instanceof OidcUser)) {
            return Optional.empty();
        }

        return Optional.of((OidcUser) authentication.getPrincipal());
    }
}
