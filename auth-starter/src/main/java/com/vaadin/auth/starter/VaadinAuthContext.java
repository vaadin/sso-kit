package com.vaadin.auth.starter;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Optional;

public interface VaadinAuthContext {
    Optional<OidcUser> getAuthenticatedUser();
}
