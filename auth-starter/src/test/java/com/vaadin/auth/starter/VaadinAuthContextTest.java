package com.vaadin.auth.starter;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for VaadinAuthContext
 */
public class VaadinAuthContextTest {

    @Test
    public void unauthenticatedContextReturnsEmptyOptional() {
        var authentication = mock(AnonymousAuthenticationToken.class);
        var securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
        var authContext = new VaadinAuthContextImpl();

        assertTrue(authContext.getAuthenticatedUser().isEmpty());
    }

    @Test
    public void authenticatedContextReturnsUserPrincipal() {
        var principal = mock(OidcUser.class);
        var authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);
        var securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
        var authContext = new VaadinAuthContextImpl();

        var user = authContext.getAuthenticatedUser().get();
        assertInstanceOf(OidcUser.class, user);
    }
}
