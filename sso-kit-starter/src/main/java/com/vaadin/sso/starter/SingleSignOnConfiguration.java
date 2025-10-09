/*-
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.sso.starter;

import java.util.Objects;

import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.security.oauth2.client.autoconfigure.ConditionalOnOAuth2ClientRegistrationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import com.vaadin.flow.spring.SpringSecurityAutoConfiguration;
import com.vaadin.sso.core.BackChannelLogoutFilter;

import static com.vaadin.flow.spring.security.VaadinSecurityConfigurer.vaadin;

/**
 * This configuration bean is provided to auto-configure Vaadin and Spring to
 * allow single sign-on against external identity providers.
 * <p>
 * It enables OAuth2/OpenID login for the identity providers defined in the
 * current application configuration and instructs the application to accept
 * requests for the login route, which can be configured setting the
 * {@code vaadin.sso.login-route} property (defaults to {@code /login}).
 * <p>
 * If you need a customized security configuration, you can disable this
 * auto-configuration class by adding its fully-qualified name to the
 * {@code spring.autoconfigure.exclude} property and provide your own
 * configuration class.
 *
 * @author Vaadin Ltd
 * @since 1.0
 */
@AutoConfiguration
@AutoConfigureBefore(SpringSecurityAutoConfiguration.class)
@EnableWebSecurity
@ConditionalOnOAuth2ClientRegistrationProperties
@EnableConfigurationProperties(SingleSignOnProperties.class)
@Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)
public class SingleSignOnConfiguration {

    private final SingleSignOnProperties properties;

    private final OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler;

    private final SessionRegistry sessionRegistry;

    private final BackChannelLogoutFilter backChannelLogoutFilter;

    /**
     * Creates an instance of this configuration bean.
     *
     * @param properties
     *            the configuration properties
     * @param sessionRegistry
     *            the session registry
     * @param clientRegistrationRepository
     *            the client-registration repository
     * @param eventPublisher
     *            the event-publisher
     */
    public SingleSignOnConfiguration(SingleSignOnProperties properties,
            SessionRegistry sessionRegistry,
            ClientRegistrationRepository clientRegistrationRepository,
            ApplicationEventPublisher eventPublisher) {
        this.properties = properties;
        this.sessionRegistry = sessionRegistry;
        this.logoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(
                clientRegistrationRepository);
        this.logoutSuccessHandler
                .setRedirectStrategy(new UidlRedirectStrategy());
        this.backChannelLogoutFilter = new BackChannelLogoutFilter(
                sessionRegistry, clientRegistrationRepository, eventPublisher);
    }

    @Bean
    public SecurityFilterChain vaadinSecurityFilterChain(HttpSecurity http) throws Exception {
        final var loginRoute = Objects.requireNonNullElse(
                properties.getLoginRoute(),
                SingleSignOnProperties.DEFAULT_LOGIN_ROUTE);
        final var logoutRedirectRoute = Objects.requireNonNullElse(
                properties.getLogoutRedirectRoute(),
                SingleSignOnProperties.DEFAULT_LOGOUT_REDIRECT_ROUTE);
        final var backChannelLogoutRoute = Objects.requireNonNullElse(
                properties.getBackChannelLogoutRoute(),
                SingleSignOnProperties.DEFAULT_BACKCHANNEL_LOGOUT_ROUTE);
        final var maximumSessions = properties.getMaximumConcurrentSessions();
        logoutSuccessHandler.setPostLogoutRedirectUri(logoutRedirectRoute);

        http.with(vaadin(),
                vaadin -> vaadin
                        .oauth2LoginPage(loginRoute, logoutRedirectRoute)
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .enableExceptionHandlingConfiguration(false));
        http.exceptionHandling(exceptionHandling -> {
            // Sets the configured login route as the entry point to redirect
            // the web browser when an authentication exception is thrown.
            var entryPoint = new LoginUrlAuthenticationEntryPoint(loginRoute);
            exceptionHandling.authenticationEntryPoint(entryPoint);
        }).sessionManagement(sessionManagement -> {
            sessionManagement.sessionConcurrency(concurrency -> {
                // Sets the maximum number of concurrent sessions per user.
                // The default is -1 which means no limit on the number of
                // concurrent sessions per user.
                concurrency.maximumSessions(maximumSessions);

                // Sets the session-registry which is used for Back-Channel
                concurrency.sessionRegistry(sessionRegistry);

                // Sets the Vaadin-Refresh token to handle expired UIDL requests
                final var expiredStrategy = new UidlExpiredSessionStrategy();
                concurrency.expiredSessionStrategy(expiredStrategy);
            });
        });

        if (properties.isBackChannelLogout()) {
            backChannelLogoutFilter
                    .setBackChannelLogoutRoute(backChannelLogoutRoute);

            // Adds the Back-Channel logout filter to the filter chain
            http.addFilterAfter(backChannelLogoutFilter, LogoutFilter.class);

            // Disable CSRF for Back-Channel logout requests
            final var matcher = backChannelLogoutFilter.getRequestMatcher();
            http.csrf(csrf -> csrf.ignoringRequestMatchers(matcher));
        } else {
            http.oidcLogout().backChannel(Customizer.withDefaults());
        }
        return http.build();
    }
}
