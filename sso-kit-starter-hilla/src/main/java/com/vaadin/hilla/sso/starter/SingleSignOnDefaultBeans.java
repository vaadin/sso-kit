/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */
package com.vaadin.hilla.sso.starter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;

import com.vaadin.sso.core.KeycloakUserMapper;

/**
 * This configuration class provides default instances for the required beans.
 *
 * @author Vaadin Ltd
 * @since 1.0
 */
@AutoConfiguration
public class SingleSignOnDefaultBeans {

    /**
     * Provides a default {@link SessionRegistry} bean.
     *
     * @return the session registry bean
     */
    @Bean
    @ConditionalOnMissingBean
    SessionRegistry getSessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * Provides a default {@link OidcUserService} bean that uses
     * {@link KeycloakUserMapper} to map Keycloak realm and client roles to
     * Spring Security granted authorities.
     *
     * @return the OIDC user service bean
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = SingleSignOnProperties.PREFIX, name = "keycloak-roles")
    OidcUserService oidcUserService() {
        var oidcUserService = new OidcUserService();
        oidcUserService.setOidcUserConverter(new KeycloakUserMapper());
        return oidcUserService;
    }
}
