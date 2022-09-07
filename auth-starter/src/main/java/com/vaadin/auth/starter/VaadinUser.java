package com.vaadin.auth.starter;

import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class VaadinUser extends DefaultOidcUser {

    VaadinUser(OidcUser oidcUser) {
        super(oidcUser.getAuthorities(), oidcUser.getIdToken());
    }
}
