/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */
package com.vaadin.sso.core;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mock.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BackChannelLogoutFilterTest {

    private static final String CLIENT_ID = "test-client";

    private static final String ISSUER_URI = "http://issuer.com";

    private static final String TOKEN_URI = ISSUER_URI + "/token";

    private final Map<String, Object> logoutTokenClaims = new HashMap<>();

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock(strictness = Strictness.LENIENT)
    private SessionRegistry sessionRegistry;

    @Mock(strictness = Strictness.LENIENT)
    private ClientRegistrationRepository clientRegistrationRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private ClientRegistration clientRegistration;

    private BackChannelLogoutFilter filter;

    @BeforeEach
    void setup() {
        // @formatter:off
        clientRegistration = ClientRegistration
                .withRegistrationId("test")
                .authorizationGrantType(
                        AuthorizationGrantType.CLIENT_CREDENTIALS)
                .clientId(CLIENT_ID)
                .issuerUri(ISSUER_URI)
                .tokenUri(TOKEN_URI)
                .build();
        // @formatter:on

        filter = new BackChannelLogoutFilter(sessionRegistry,
                clientRegistrationRepository, eventPublisher,
                this::createJwtDecoder);

        when(request.getHttpServletMapping()).thenCallRealMethod();
        when(clientRegistrationRepository.findByRegistrationId("test"))
                .thenReturn(clientRegistration);
        when(clientRegistrationRepository.findByRegistrationId(not(eq("test"))))
                .thenReturn(null);
    }

    @AfterEach
    void cleanup() {
        logoutTokenClaims.clear();
    }

    @Test
    void nonMatchingRequest_doesNotPerformLogout()
            throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/foo");

        filter.doFilter(request, response, chain);

        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void invalidRegistrationId_badRequest()
            throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/logout/back-channel/foo");

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void missingTokenParameter_badRequest()
            throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/logout/back-channel/test");

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void withSidClaim_onlyMatchingSessionsSetToExpire()
            throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/logout/back-channel/test");
        when(request.getParameter(BackChannelLogoutFilter.TOKEN_PARAM_NAME))
                .thenReturn("token");

        final var matchingOidcUser = mock(OidcUser.class);
        when(matchingOidcUser.getClaimAsString("sid")).thenReturn("1234");

        final var nonMatchingOidcUser = mock(OidcUser.class);
        when(nonMatchingOidcUser.getClaimAsString("sid")).thenReturn("5678");

        when(sessionRegistry.getAllPrincipals())
                .thenReturn(List.of(matchingOidcUser, nonMatchingOidcUser));

        final var matchingSession = createSessionSpy(matchingOidcUser);
        final var nonMatchingSession = createSessionSpy(nonMatchingOidcUser);

        when(sessionRegistry.getAllSessions(matchingOidcUser, false))
                .thenReturn(List.of(matchingSession));
        when(sessionRegistry.getAllSessions(nonMatchingOidcUser, false))
                .thenReturn(List.of(nonMatchingSession));

        // Adds the SID claim to the logout token before filtering
        addClaimToLogoutToken(LogoutTokenClaimNames.SID, "1234");

        filter.doFilter(request, response, chain);

        verify(matchingSession).expireNow();
        verify(nonMatchingSession, never()).expireNow();
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void withSubClaim_onlyMatchingSessionsSetToExpire()
            throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/logout/back-channel/test");
        when(request.getParameter(BackChannelLogoutFilter.TOKEN_PARAM_NAME))
                .thenReturn("token");

        final var matchingOidcUser = mock(OidcUser.class);
        when(matchingOidcUser.getSubject()).thenReturn("john");

        final var nonMatchingOidcUser = mock(OidcUser.class);
        when(nonMatchingOidcUser.getSubject()).thenReturn("dave");

        when(sessionRegistry.getAllPrincipals())
                .thenReturn(List.of(matchingOidcUser, nonMatchingOidcUser));

        final var matchingSession = createSessionSpy(matchingOidcUser);
        final var nonMatchingSession = createSessionSpy(nonMatchingOidcUser);

        when(sessionRegistry.getAllSessions(matchingOidcUser, false))
                .thenReturn(List.of(matchingSession));
        when(sessionRegistry.getAllSessions(nonMatchingOidcUser, false))
                .thenReturn(List.of(nonMatchingSession));

        filter.doFilter(request, response, chain);

        verify(matchingSession).expireNow();
        verify(nonMatchingSession, never()).expireNow();
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    private SessionInformation createSessionSpy(Object principal) {
        final var now = Date.from(Instant.now());
        return spy(new SessionInformation(principal, "foo", now));
    }

    private void addClaimToLogoutToken(String claimName, Object claimValue) {
        logoutTokenClaims.put(claimName, claimValue);
    }

    private JwtDecoder createJwtDecoder(ClientRegistration clientRegistration) {
        // @formatter:off
        return token -> Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .issuer(ISSUER_URI)
                .issuedAt(Instant.now())
                .audience(List.of(CLIENT_ID))
                .subject("john")
                .claim(LogoutTokenClaimNames.EVENTS, Map.of("http://schemas.openid.net/event/backchannel-logout", Map.of()))
                .claims(map -> map.putAll(logoutTokenClaims))
                .build();
        // @formatter:on
    }
}
