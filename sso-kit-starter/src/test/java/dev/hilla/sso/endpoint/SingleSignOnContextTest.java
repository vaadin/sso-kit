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

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.vaadin.sso.starter.SingleSignOnProperties;

import jakarta.servlet.http.HttpServletRequest;

public class SingleSignOnContextTest {

    @Mock
    private final ClientRegistrationRepository defaultClientRegistrationRepository = mock(
            ClientRegistrationRepository.class);

    @Mock
    private final SingleSignOnProperties defaultProperties = mock(
            SingleSignOnProperties.class);

    @Mock
    private final BackChannelLogoutSubscription defaultBackChannelLogoutSubscription = mock(
            BackChannelLogoutSubscription.class);

    @Test
    public void testGetOidcUser() {
        var securityContext = mock(SecurityContext.class);
        var authentication = mock(Authentication.class);
        var ou = mock(OidcUser.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(ou);
        SecurityContextHolder.setContext(securityContext);
        var user = SingleSignOnContext.getOidcUser();
        assertEquals(ou, user.orElseThrow());
    }

    @Test
    public void testGetCurrentHttpRequest() {
        var attributes = mock(ServletRequestAttributes.class);
        var request = mock(HttpServletRequest.class);
        when(attributes.getRequest()).thenReturn(request);
        RequestContextHolder.setRequestAttributes(attributes);
        var result = SingleSignOnContext.getCurrentHttpRequest();
        assertEquals(request, result.orElseThrow());
    }

    @Test
    public void testGetRegisteredProviders() {
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
    public void testIsBackChannelLogoutEnabled() {
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
    public void testGetBackChannelLogoutFlux() {
        var securityContext = mock(SecurityContext.class);
        var authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("foo");
        SecurityContextHolder.setContext(securityContext);

        var subscription = mock(BackChannelLogoutSubscription.class);

        var context = new SingleSignOnContext(
                defaultClientRegistrationRepository, defaultProperties,
                subscription);
        context.getBackChannelLogoutFlux();
        verify(subscription).getFluxForUser("foo");
    }
}
