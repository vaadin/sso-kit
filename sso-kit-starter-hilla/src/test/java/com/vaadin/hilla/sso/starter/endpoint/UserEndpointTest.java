/*-
 * Copyright (C) 2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.hilla.sso.starter.endpoint;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.vaadin.hilla.sso.starter.SingleSignOnContext;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

public class UserEndpointTest {

    @Test
    public void getAuthenticatedUser_returnsTheExpectedUser() {
        try (MockedStatic<SingleSignOnContext> singleSignOnContextMockedStatic = mockStatic(
                SingleSignOnContext.class)) {
            var userEndpoint = new UserEndpoint();
            singleSignOnContextMockedStatic
                    .when(SingleSignOnContext::getOidcUser)
                    .thenReturn(Optional.of(createDefaultOidcUser()));
            Optional<User> user = userEndpoint.getAuthenticatedUser();
            user.ifPresent(u -> assertEquals("Test User", u.getFullName()));
        }
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
