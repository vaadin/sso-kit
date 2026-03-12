/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */
package com.vaadin.sso.core;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserSource;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails.UserInfoEndpoint;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.jwt.Jwt;

import com.vaadin.sso.core.KeycloakUserMapper.KeycloakClientRole;
import com.vaadin.sso.core.KeycloakUserMapper.KeycloakRealmRole;
import com.vaadin.sso.core.KeycloakUserMapper.OidcScopeAuthority;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakUserMapperTest {

    private static final String CLIENT_ID = "test-client";

    private static final String ISSUER_URI = "http://localhost:8080/realms/test";

    @Mock
    private OidcUserSource userSource;

    @Mock
    private OidcUserRequest userRequest;

    @Mock
    private OidcUserInfo userInfo;

    @Mock
    private OidcIdToken idToken;

    @Mock
    private ClientRegistration clientRegistration;

    @Mock
    private ProviderDetails providerDetails;

    @Mock
    private UserInfoEndpoint userInfoEndpoint;

    @Mock
    private OAuth2AccessToken accessToken;

    private KeycloakUserMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new KeycloakUserMapper(registration -> this::createJwt);

        when(userSource.getUserRequest()).thenReturn(userRequest);
        when(userSource.getUserInfo()).thenReturn(userInfo);
        when(userRequest.getClientRegistration())
                .thenReturn(clientRegistration);
        when(userRequest.getAccessToken()).thenReturn(accessToken);
        when(userRequest.getIdToken()).thenReturn(idToken);
        when(clientRegistration.getClientId()).thenReturn(CLIENT_ID);
        when(clientRegistration.getProviderDetails())
                .thenReturn(providerDetails);
        when(providerDetails.getUserInfoEndpoint())
                .thenReturn(userInfoEndpoint);
        when(accessToken.getTokenValue()).thenReturn("token");
        when(accessToken.getScopes())
                .thenReturn(Set.of("openid", "profile", "email"));
        when(idToken.getClaims())
                .thenReturn(Map.of("sub", "user-123", "iss", ISSUER_URI));
    }

    @Test
    void convert_mapsScopes() {
        var user = mapper.convert(userSource);

        @SuppressWarnings("unchecked")
        var authorities = (Collection<GrantedAuthority>) user.getAuthorities();
        assertThat(authorities).contains(new OidcScopeAuthority("openid"),
                new OidcScopeAuthority("profile"),
                new OidcScopeAuthority("email"));
    }

    @Test
    void convert_mapsRealmRoles() {
        jwtClaims.put(KeycloakUserMapper.REALM_ACCESS_CLAIM, Map
                .of(KeycloakUserMapper.ROLES_CLAIM, List.of("admin", "user")));

        var user = mapper.convert(userSource);

        @SuppressWarnings("unchecked")
        var authorities = (Collection<GrantedAuthority>) user.getAuthorities();
        assertThat(authorities).contains(new KeycloakRealmRole("admin"),
                new KeycloakRealmRole("user"));
    }

    @Test
    void convert_mapsClientRoles() {
        jwtClaims.put(KeycloakUserMapper.RESOURCE_ACCESS_CLAIM,
                Map.of(CLIENT_ID, Map.of(KeycloakUserMapper.ROLES_CLAIM,
                        List.of("manage-account", "view-profile"))));

        var user = mapper.convert(userSource);

        @SuppressWarnings("unchecked")
        var authorities = (Collection<GrantedAuthority>) user.getAuthorities();
        assertThat(authorities).contains(
                new KeycloakClientRole(CLIENT_ID, "manage-account"),
                new KeycloakClientRole(CLIENT_ID, "view-profile"));
    }

    @Test
    void convert_mapsRealmAndClientRoles() {
        jwtClaims.put(KeycloakUserMapper.REALM_ACCESS_CLAIM,
                Map.of(KeycloakUserMapper.ROLES_CLAIM, List.of("admin")));
        jwtClaims.put(KeycloakUserMapper.RESOURCE_ACCESS_CLAIM,
                Map.of(CLIENT_ID, Map.of(KeycloakUserMapper.ROLES_CLAIM,
                        List.of("manage-account"))));

        var user = mapper.convert(userSource);

        @SuppressWarnings("unchecked")
        var authorities = (Collection<GrantedAuthority>) user.getAuthorities();
        assertThat(authorities).contains(new KeycloakRealmRole("admin"),
                new KeycloakClientRole(CLIENT_ID, "manage-account"));
    }

    @Test
    void convert_noRealmAccess_noRealmRoles() {
        var user = mapper.convert(userSource);

        @SuppressWarnings("unchecked")
        var authorities = (Collection<GrantedAuthority>) user.getAuthorities();
        assertThat(authorities).noneMatch(a -> a instanceof KeycloakRealmRole);
    }

    @Test
    void convert_noResourceAccess_noClientRoles() {
        var user = mapper.convert(userSource);

        @SuppressWarnings("unchecked")
        var authorities = (Collection<GrantedAuthority>) user.getAuthorities();
        assertThat(authorities).noneMatch(a -> a instanceof KeycloakClientRole);
    }

    @Test
    void convert_otherClientResourceAccess_ignored() {
        jwtClaims.put(KeycloakUserMapper.RESOURCE_ACCESS_CLAIM, Map.of(
                "other-client",
                Map.of(KeycloakUserMapper.ROLES_CLAIM, List.of("some-role"))));

        var user = mapper.convert(userSource);

        @SuppressWarnings("unchecked")
        var authorities = (Collection<GrantedAuthority>) user.getAuthorities();
        assertThat(authorities).noneMatch(a -> a instanceof KeycloakClientRole);
    }

    @Test
    void convert_withUserNameAttribute_usesIt() {
        when(userInfoEndpoint.getUserNameAttributeName())
                .thenReturn("preferred_username");
        when(idToken.getClaims()).thenReturn(Map.of("sub", "user-123", "iss",
                ISSUER_URI, "preferred_username", "john"));

        var user = mapper.convert(userSource);

        assertThat(user.getName()).isEqualTo("john");
    }

    @Test
    void convert_withoutUserNameAttribute_usesSubject() {
        var user = mapper.convert(userSource);

        assertThat(user.getSubject()).isEqualTo("user-123");
    }

    private final HashMap<String, Object> jwtClaims = new HashMap<>();

    private Jwt createJwt(String tokenValue) {
        var issuedAt = Instant.parse("2025-01-01T00:00:00Z");
        // @formatter:off
        return Jwt.withTokenValue(tokenValue)
                .header("alg", "RS256")
                .issuer(ISSUER_URI)
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(60))
                .subject("user-123")
                .claims(map -> map.putAll(jwtClaims))
                .build();
        // @formatter:on
    }
}
