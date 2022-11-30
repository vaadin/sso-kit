package com.vaadin.sso.starter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.util.StringUtils;

public class SingleSignOnUserService extends OidcUserService {

    private static final String REALM_ACCESS_CLAIM = "realm_access";

    private static final String ROLES_KEY = "roles";

    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest)
            throws OAuth2AuthenticationException {
        final var user = super.loadUser(userRequest);
        return decorateUser(user, userRequest);
    }

    protected OidcUser decorateUser(OidcUser user,
            OidcUserRequest userRequest) {
        final var userInfo = user.getUserInfo();
        final var idToken = userRequest.getIdToken();

        final var authorities = new HashSet<GrantedAuthority>();

        authorities.addAll(user.getAuthorities());

        if (userInfo.hasClaim(REALM_ACCESS_CLAIM)) {
            final var keycloakRoles = getKeycloakRoles(userInfo, idToken);
            authorities.addAll(keycloakRoles);
        }

        final var userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName();

        return StringUtils.hasText(userNameAttributeName)
                ? new DefaultOidcUser(authorities, user.getIdToken(),
                        user.getUserInfo(), userNameAttributeName)
                : new DefaultOidcUser(authorities, user.getIdToken(),
                        user.getUserInfo());
    }

    @SuppressWarnings("unchecked")
    private List<OidcUserAuthority> getKeycloakRoles(OidcUserInfo userInfo,
            OidcIdToken idToken) {
        final var realmAccessClaim = userInfo.getClaimAsMap(REALM_ACCESS_CLAIM);
        final var roles = (Collection<String>) realmAccessClaim.get(ROLES_KEY);
        return roles.stream()
                .map(role -> new OidcUserAuthority(ROLE_PREFIX + role, idToken,
                        userInfo))
                .collect(Collectors.toList());
    }
}
