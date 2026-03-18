/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */
package com.vaadin.sso.core;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserSource;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.util.StringUtils;

/**
 * A converter that maps Keycloak-specific JWT claims to Spring Security
 * {@link GrantedAuthority} instances.
 * <p>
 * This converter extracts:
 * <ul>
 * <li>OAuth2 scopes from the access token as {@code SCOPE_} authorities</li>
 * <li>Keycloak realm roles from the {@code realm_access} claim as {@code ROLE_}
 * authorities</li>
 * <li>Keycloak client roles from the {@code resource_access} claim as
 * {@code ROLE_} authorities</li>
 * </ul>
 *
 * @author Vaadin Ltd
 * @since 4.0
 */
public class KeycloakUserMapper implements Converter<OidcUserSource, OidcUser> {

    static final String REALM_ACCESS_CLAIM = "realm_access";

    static final String RESOURCE_ACCESS_CLAIM = "resource_access";

    static final String ROLES_CLAIM = "roles";

    static final String ROLE_PREFIX = "ROLE_";

    static final String SCOPE_PREFIX = "SCOPE_";

    private final JwtDecoderFactory<ClientRegistration> decoderFactory;

    /**
     * Creates a new instance that uses the default
     * {@link NimbusJwtDecoder}-based JWT decoder.
     */
    public KeycloakUserMapper() {
        this(clientRegistration -> {
            var providerDetails = clientRegistration.getProviderDetails();
            var jwkSetUri = providerDetails.getJwkSetUri();
            var issuerUri = providerDetails.getIssuerUri();
            var jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
            jwtDecoder.setJwtValidator(
                    JwtValidators.createDefaultWithIssuer(issuerUri));
            return jwtDecoder;
        });
    }

    KeycloakUserMapper(JwtDecoderFactory<ClientRegistration> decoderFactory) {
        this.decoderFactory = decoderFactory;
    }

    @Override
    public OidcUser convert(OidcUserSource userSource) {
        var userRequest = userSource.getUserRequest();
        var userInfo = userSource.getUserInfo();
        var authorities = new LinkedHashSet<GrantedAuthority>();
        var accessToken = userRequest.getAccessToken();
        accessToken.getScopes().stream().map(OidcScopeAuthority::new)
                .forEach(authorities::add);
        var clientRegistration = userRequest.getClientRegistration();
        var clientId = clientRegistration.getClientId();
        var jwtDecoder = decoderFactory.createDecoder(clientRegistration);
        var jwt = jwtDecoder.decode(accessToken.getTokenValue());
        if (jwt.hasClaim(REALM_ACCESS_CLAIM)) {
            var roles = extractRoles(jwt.getClaimAsMap(REALM_ACCESS_CLAIM));
            roles.stream().map(KeycloakRealmRole::new)
                    .forEach(authorities::add);
        }
        if (jwt.hasClaim(RESOURCE_ACCESS_CLAIM)) {
            var resourceAccess = jwt.getClaimAsMap(RESOURCE_ACCESS_CLAIM);
            var clientAccess = asMap(resourceAccess.getOrDefault(clientId,
                    Collections.emptyMap()));
            var roles = extractRoles(clientAccess);
            roles.stream().map(role -> new KeycloakClientRole(clientId, role))
                    .forEach(authorities::add);
        }
        var providerDetails = clientRegistration.getProviderDetails();
        var userNameAttributeName = providerDetails.getUserInfoEndpoint()
                .getUserNameAttributeName();
        if (StringUtils.hasText(userNameAttributeName)) {
            authorities.add(new OidcUserAuthority(userRequest.getIdToken(),
                    userInfo, userNameAttributeName));
            return new DefaultOidcUser(authorities, userRequest.getIdToken(),
                    userNameAttributeName);
        } else {
            authorities.add(
                    new OidcUserAuthority(userRequest.getIdToken(), userInfo));
            return new DefaultOidcUser(authorities, userRequest.getIdToken(),
                    userInfo);
        }
    }

    @SuppressWarnings("unchecked")
    private static List<String> extractRoles(Map<String, Object> claim) {
        var roles = claim.get(ROLES_CLAIM);
        if (roles instanceof List<?>) {
            return (List<String>) roles;
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object value) {
        if (value instanceof Map<?, ?>) {
            return (Map<String, Object>) value;
        }
        return Collections.emptyMap();
    }

    record OidcScopeAuthority(String scope) implements GrantedAuthority {

        @Override
        public String getAuthority() {
            return SCOPE_PREFIX + scope;
        }
    }

    record KeycloakRealmRole(String role) implements GrantedAuthority {

        @Override
        public String getAuthority() {
            return ROLE_PREFIX + role;
        }
    }

    record KeycloakClientRole(String client,
            String role) implements GrantedAuthority {

        @Override
        public String getAuthority() {
            return ROLE_PREFIX + role;
        }
    }
}
