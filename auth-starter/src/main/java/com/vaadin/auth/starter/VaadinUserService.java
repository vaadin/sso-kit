package com.vaadin.auth.starter;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

public class VaadinUserService extends OidcUserService {

    @Override
    public VaadinUser loadUser(OidcUserRequest userRequest)
            throws OAuth2AuthenticationException {
        final var oidcUser = super.loadUser(userRequest);
        return new VaadinUser(oidcUser);
    }
}
