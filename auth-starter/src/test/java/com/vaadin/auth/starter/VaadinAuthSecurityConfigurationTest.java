package com.vaadin.auth.starter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import com.vaadin.auth.starter.VaadinAuthSecurityConfiguration.DefaultVaadinAuthContext;
import com.vaadin.flow.spring.SpringBootAutoConfiguration;
import com.vaadin.flow.spring.SpringSecurityAutoConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test suite for {@link VaadinAuthSecurityConfiguration}.
 */
@ExtendWith(MockitoExtension.class)
public class VaadinAuthSecurityConfigurationTest {

    private WebApplicationContextRunner contextRunner;

    @Mock
    private ClientRegistrationRepository clientRegistrationRepository;

    @BeforeEach
    public void init() {
        contextRunner = new WebApplicationContextRunner()
                .withConfiguration(
                        AutoConfigurations.of(SpringBootAutoConfiguration.class,
                                SpringSecurityAutoConfiguration.class,
                                VaadinAuthSecurityConfiguration.class))
                .withBean(ClientRegistrationRepository.class,
                        () -> clientRegistrationRepository);
    }

    @Test
    public void autoConfigureProperty_notSet_configurationEnabled() {
        contextRunner.run(ctx -> {
            assertThat(ctx)
                    .hasSingleBean(VaadinAuthSecurityConfiguration.class);
        });
    }

    @Test
    public void autoConfigureProperty_isFalse_configurationDisabled() {
        contextRunner.withPropertyValues("vaadin.auth.auto-configure=false")
                .run(ctx -> {
                    assertThat(ctx).doesNotHaveBean(
                            VaadinAuthSecurityConfiguration.class);
                });
    }

    @Test
    public void loginRouteProperty_hasDefaultValue() {
        contextRunner.run(ctx -> {
            String loginRoute = ctx.getBean(VaadinAuthProperties.class)
                    .getLoginRoute();
            assertEquals(VaadinAuthProperties.DEFAULT_LOGIN_ROUTE, loginRoute);
        });
    }

    @Test
    public void loginRouteProperty_hasCustomValue() {
        contextRunner.withPropertyValues("vaadin.auth.login-route=/custom")
                .run(ctx -> {
                    String loginRoute = ctx.getBean(VaadinAuthProperties.class)
                            .getLoginRoute();
                    assertEquals("/custom", loginRoute);
                });
    }

    @Test
    public void logoutHandlersAreSetOnAuthContext() {
        contextRunner.run(ctx -> {
            var authCtx = (DefaultVaadinAuthContext) ctx
                    .getBean(VaadinAuthContext.class);
            var successfulLogoutHandler = authCtx.getLogoutSuccessHandler();
            var logoutHandler = authCtx.getLogoutHandler();

            // Assert that the correct logout success handler has been set
            assertInstanceOf(OidcClientInitiatedLogoutSuccessHandler.class,
                    successfulLogoutHandler);

            // Assert that a logout handler has been set
            assertNotNull(logoutHandler);
        });
    }
}
