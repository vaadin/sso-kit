/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */
package com.vaadin.sso.starter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.flow.spring.SpringBootAutoConfiguration;
import com.vaadin.flow.spring.SpringSecurityAutoConfiguration;
import com.vaadin.sso.core.BackChannelLogoutFilter;
import com.vaadin.sso.core.KeycloakUserMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for {@link SingleSignOnConfiguration}.
 */
@ExtendWith(MockitoExtension.class)
class SingleSignOnConfigurationTest {

    private WebApplicationContextRunner contextRunner;

    @Mock
    private ClientRegistrationRepository clientRegistrationRepository;

    @BeforeEach
    void init() {
        contextRunner = new WebApplicationContextRunner()
                .withConfiguration(
                        AutoConfigurations.of(SpringBootAutoConfiguration.class,
                                SpringSecurityAutoConfiguration.class,
                                SingleSignOnConfiguration.class,
                                SingleSignOnDefaultBeans.class))
                .withPropertyValues(
                        "spring.security.oauth2.client.registration.foo.client-id=foo")
                .withBean(ClientRegistrationRepository.class,
                        () -> clientRegistrationRepository);
    }

    @Test
    void clientRepository_notAvailable_configurationDisabled() {
        final var runner = new WebApplicationContextRunner().withConfiguration(
                AutoConfigurations.of(SpringBootAutoConfiguration.class,
                        SpringSecurityAutoConfiguration.class,
                        SingleSignOnConfiguration.class,
                        SingleSignOnDefaultBeans.class));
        runner.run(ctx -> assertThat(ctx)
                .doesNotHaveBean(SingleSignOnConfiguration.class));
    }

    @Test
    void loginRouteProperty_hasDefaultValue() {
        contextRunner.run(ctx -> {
            var loginRoute = ctx.getBean(SingleSignOnProperties.class)
                    .getLoginRoute();
            assertEquals(SingleSignOnProperties.DEFAULT_LOGIN_ROUTE,
                    loginRoute);
        });
    }

    @Test
    void loginRouteProperty_hasCustomValue() {
        contextRunner.withPropertyValues("vaadin.sso.login-route=/custom")
                .run(ctx -> {
                    var loginRoute = ctx.getBean(SingleSignOnProperties.class)
                            .getLoginRoute();
                    assertEquals("/custom", loginRoute);
                });
    }

    @Test
    void backChannelLogout_isTrue_backChannelLogoutFilterConfigured() {
        contextRunner.withPropertyValues("vaadin.sso.back-channel-logout=true")
                .run(ctx -> {
                    var filterChain = (SecurityFilterChain) ctx
                            .getBean("vaadinSecurityFilterChain");
                    assertTrue(filterChain.getFilters().stream().anyMatch(
                            filter -> filter instanceof BackChannelLogoutFilter));
                });
    }

    @Test
    void backChannelLogout_isFalse_backChannelLogoutFilterNotConfigured() {
        contextRunner.withPropertyValues("vaadin.sso.back-channel-logout=false")
                .run(ctx -> {
                    var filterChain = (SecurityFilterChain) ctx
                            .getBean("vaadinSecurityFilterChain");
                    assertTrue(filterChain.getFilters().stream().noneMatch(
                            filter -> filter instanceof BackChannelLogoutFilter));
                });
    }

    @Test
    void keycloakRoles_isTrue_oidcUserServiceHasKeycloakUserMapper() {
        contextRunner.withPropertyValues("vaadin.sso.keycloak-roles=true")
                .run(ctx -> {
                    assertThat(ctx).hasSingleBean(OidcUserService.class);
                    var oidcUserService = ctx.getBean(OidcUserService.class);
                    var oidcUserConverter = ReflectionTestUtils
                            .getField(oidcUserService, "oidcUserConverter");
                    assertThat(oidcUserConverter)
                            .isInstanceOf(KeycloakUserMapper.class);
                });
    }

    @Test
    void keycloakRoles_isFalse_noOidcUserServiceBean() {
        contextRunner.run(
                ctx -> assertThat(ctx).doesNotHaveBean(OidcUserService.class));
    }
}
