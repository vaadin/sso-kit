/*-
 * Copyright (C) 2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.hilla.sso.starter;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SingleSignOnContextTest {

    @Mock
    private ClientRegistrationRepository defaultClientRegistrationRepository;

    @Mock
    private SingleSignOnProperties defaultProperties;

    @Mock
    private BackChannelLogoutSubscription defaultBackChannelLogoutSubscription;

    @Test
    void getOidcUser_returnsUser() {
        var securityContext = mock(SecurityContext.class);
        var authentication = mock(Authentication.class);
        var oidcUser = mock(OidcUser.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        SecurityContextHolder.setContext(securityContext);

        var user = SingleSignOnContext.getOidcUser();

        assertEquals(oidcUser, user.orElseThrow());
    }

    @Test
    void getCurrentHttpRequest_returnsHttpServletRequest() {
        var attributes = mock(ServletRequestAttributes.class);
        var request = mock(HttpServletRequest.class);

        when(attributes.getRequest()).thenReturn(request);
        RequestContextHolder.setRequestAttributes(attributes);

        var result = SingleSignOnContext.getCurrentHttpRequest();

        assertEquals(request, result.orElseThrow());
    }

    @Test
    void getRegisteredProviders_returnsAListOfRegisteredProviders() {
        var names = List.of("google", "facebook");
        var repository = mock(InMemoryClientRegistrationRepository.class);

        when(repository.iterator()).thenReturn(names.stream()
                .map(name -> ClientRegistration.withRegistrationId(name)
                        .authorizationGrantType(
                                AuthorizationGrantType.CLIENT_CREDENTIALS)
                        .clientId(name + "-id")
                        .tokenUri("https://" + name + ".com/oauth2/token")
                        .build())
                .iterator());

        var context = new SingleSignOnContext(repository, defaultProperties,
                defaultBackChannelLogoutSubscription);
        var result = context.getRegisteredProviders();

        assertEquals(names, result);
    }

    @Test
    void getSingleSignOnData_returnsPopulatedData() {
        var properties = mock(SingleSignOnProperties.class);
        var securityContext = mock(SecurityContext.class);
        var authenticationToken = mock(OAuth2AuthenticationToken.class);
        var attributes = mock(ServletRequestAttributes.class);
        var request = mock(HttpServletRequest.class);
        var repository = mock(InMemoryClientRegistrationRepository.class);
        var clientRegistration = ClientRegistration.withRegistrationId("vaadin")
                .authorizationGrantType(
                        AuthorizationGrantType.CLIENT_CREDENTIALS)
                .providerConfigurationMetadata(Map.of("end_session_endpoint",
                        "https://vaadin.com/oauth2/logout"))
                .clientId("vaadin-id")
                .tokenUri("https://vaadin.com/oauth2/token").build();
        var oidcUser = createDefaultOidcUser();

        when(properties.getLoginRoute()).thenReturn("login");
        when(properties.isBackChannelLogout()).thenReturn(true);
        when(properties.getLogoutRedirectRoute()).thenReturn("{baseUrl}",
                "logout");
        when(repository.findByRegistrationId(anyString()))
                .thenReturn(clientRegistration);
        when(authenticationToken.getPrincipal()).thenReturn(oidcUser);
        when(authenticationToken.getAuthorizedClientRegistrationId())
                .thenReturn("vaadin");
        when(securityContext.getAuthentication())
                .thenReturn(authenticationToken);
        when(attributes.getRequest()).thenReturn(request);
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("vaadin.com");
        when(request.getServerPort()).thenReturn(80);
        when(request.getRequestURI()).thenReturn("/");
        when(request.getContextPath()).thenReturn("/logout");
        RequestContextHolder.setRequestAttributes(attributes);
        SecurityContextHolder.setContext(securityContext);

        var context = new SingleSignOnContext(repository, properties,
                defaultBackChannelLogoutSubscription);
        var result = List.of(context.getSingleSignOnData(),
                context.getSingleSignOnData());

        assertTrue(result.get(0).isAuthenticated());
        assertTrue(result.get(0).isBackChannelLogoutEnabled());
        assertEquals("login", result.get(0).getLoginLink());
        assertEquals(List.of(
                "https://vaadin.com/oauth2/logout?id_token_hint=tokenValue&post_logout_redirect_uri=https://vaadin.com:80/logout",
                "https://vaadin.com/oauth2/logout?id_token_hint=tokenValue&post_logout_redirect_uri=logout"),
                List.of(result.get(0).getLogoutLink(),
                        result.get(1).getLogoutLink()));
        assertEquals("USER", result.get(0).getRoles().getFirst());
    }

    @Test
    void isBackChannelLogoutEnabled_returnsTheCorrectValue() {
        var properties = mock(SingleSignOnProperties.class);

        when(properties.isBackChannelLogout()).thenReturn(true, false);

        var context = new SingleSignOnContext(
                defaultClientRegistrationRepository, properties,
                defaultBackChannelLogoutSubscription);
        var result = List.of(context.isBackChannelLogoutEnabled(),
                context.isBackChannelLogoutEnabled());

        verify(properties, times(2)).isBackChannelLogout();
        assertEquals(List.of(true, false), result);
    }

    @Test
    void getBackChannelLogoutFlux_returnsFluxMessage() {
        var securityContext = mock(SecurityContext.class);
        var authentication = mock(Authentication.class);
        var subscription = mock(BackChannelLogoutSubscription.class);
        var message = new BackChannelLogoutSubscription.Message(
                "User logged out");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("user");
        when(subscription.getFluxForUser(any())).thenReturn(Flux.just(message));
        SecurityContextHolder.setContext(securityContext);

        var context = new SingleSignOnContext(
                defaultClientRegistrationRepository, defaultProperties,
                subscription);
        var messageFlux = context.getBackChannelLogoutFlux();

        assertEquals(message, messageFlux.blockFirst());
    }

    private DefaultOidcUser createDefaultOidcUser() {
        var grantedAuthorities = List
                .of(new SimpleGrantedAuthority("ROLE_USER"));
        var oidcIdToken = new OidcIdToken("tokenValue", Instant.now(),
                Instant.now().plus(Duration.ofDays(6)),
                Map.of("sub", "subValue"));
        var oidcUserInfo = new OidcUserInfo(Map.of("name", "Test User"));
        return new DefaultOidcUser(grantedAuthorities, oidcIdToken,
                oidcUserInfo);
    }
}
