package com.vaadin.auth.starter;

import javax.servlet.ServletException;

import java.io.IOException;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.CompositeLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessEventPublishingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletResponse;

public class VaadinAuthContextImpl implements VaadinAuthContext {

    private final VaadinAuthProperties properties;

    private final ApplicationEventPublisher eventPublisher;

    private final ClientRegistrationRepository clientRegistrationRepository;

    /**
     * Default implementation of the {@link VaadinAuthContext} interface.
     */
    public VaadinAuthContextImpl(VaadinAuthProperties properties,
            ApplicationEventPublisher eventPublisher,
            ClientRegistrationRepository clientRegistrationRepository) {
        this.properties = properties;
        this.eventPublisher = eventPublisher;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public Optional<OidcUser> getAuthenticatedUser() {
        return Optional.of(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .filter(OidcUser.class::isInstance).map(OidcUser.class::cast);
    }

    @Override
    public void logout() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var logoutHandler = new SecurityContextLogoutHandler();
        var eventPublishingHandler = new LogoutSuccessEventPublishingLogoutHandler();
        eventPublishingHandler.setApplicationEventPublisher(eventPublisher);
        var handler = new CompositeLogoutHandler(logoutHandler,
                eventPublishingHandler);
        var req = VaadinServletRequest.getCurrent().getHttpServletRequest();
        var res = VaadinServletResponse.getCurrent().getHttpServletResponse();
        handler.logout(req, res, auth);
        var oidcLogoutHandler = new OidcClientInitiatedLogoutSuccessHandler(
                clientRegistrationRepository);
        oidcLogoutHandler
                .setPostLogoutRedirectUri(properties.getLogoutRedirectUrl());
        try {
            oidcLogoutHandler.onLogoutSuccess(req, res, auth);
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
