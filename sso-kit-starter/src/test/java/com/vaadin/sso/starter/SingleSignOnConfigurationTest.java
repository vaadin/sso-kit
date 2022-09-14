package com.vaadin.sso.starter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import com.vaadin.flow.spring.SpringBootAutoConfiguration;
import com.vaadin.flow.spring.SpringSecurityAutoConfiguration;
import com.vaadin.sso.starter.SingleSignOnConfiguration.DefaultVaadinAuthContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test suite for {@link SingleSignOnConfiguration}.
 */
@ExtendWith(MockitoExtension.class)
public class SingleSignOnConfigurationTest {

    private WebApplicationContextRunner contextRunner;

    @Mock
    private ClientRegistrationRepository clientRegistrationRepository;

    @BeforeEach
    public void init() {
        contextRunner = new WebApplicationContextRunner()
                .withConfiguration(
                        AutoConfigurations.of(SpringBootAutoConfiguration.class,
                                SpringSecurityAutoConfiguration.class,
                                SingleSignOnConfiguration.class))
                .withBean(ClientRegistrationRepository.class,
                        () -> clientRegistrationRepository);
    }

    @Test
    public void autoConfigureProperty_notSet_configurationEnabled() {
        contextRunner.run(ctx -> {
            assertThat(ctx).hasSingleBean(SingleSignOnConfiguration.class);
        });
    }

    @Test
    public void autoConfigureProperty_isFalse_configurationDisabled() {
        contextRunner.withPropertyValues("vaadin.sso.auto-configure=false")
                .run(ctx -> {
                    assertThat(ctx)
                            .doesNotHaveBean(SingleSignOnConfiguration.class);
                });
    }

    @Test
    public void loginRouteProperty_hasDefaultValue() {
        contextRunner.run(ctx -> {
            String loginRoute = ctx.getBean(SingleSignOnProperties.class)
                    .getLoginRoute();
            assertEquals(SingleSignOnProperties.DEFAULT_LOGIN_ROUTE,
                    loginRoute);
        });
    }

    @Test
    public void loginRouteProperty_hasCustomValue() {
        contextRunner.withPropertyValues("vaadin.sso.login-route=/custom")
                .run(ctx -> {
                    String loginRoute = ctx
                            .getBean(SingleSignOnProperties.class)
                            .getLoginRoute();
                    assertEquals("/custom", loginRoute);
                });
    }

    @Test
    public void logoutHandlersAreSetOnAuthContext() {
        contextRunner.run(ctx -> {
            var authCtx = (DefaultVaadinAuthContext) ctx
                    .getBean(AuthenticationContext.class);
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
