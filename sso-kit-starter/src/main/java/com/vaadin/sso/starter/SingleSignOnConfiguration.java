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

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.ClientsConfiguredCondition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Conditional;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.savedrequest.RequestCache;

import com.vaadin.flow.spring.security.VaadinSavedRequestAwareAuthenticationSuccessHandler;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import com.vaadin.sso.core.BackChannelLogoutFilter;

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
 * auto-configuration class by setting the {@code vaadin.sso.auto-configure}
 * property to {@code false} and provide your own configuration class.
 *
 * @author Vaadin Ltd
 * @since 1.0
 */
@AutoConfiguration
@EnableWebSecurity
@Conditional(ClientsConfiguredCondition.class)
@EnableConfigurationProperties(SingleSignOnProperties.class)
public class SingleSignOnConfiguration extends VaadinWebSecurity {

    private final SingleSignOnProperties properties;

    private final OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler;

    private final VaadinSavedRequestAwareAuthenticationSuccessHandler loginSuccessHandler;

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
        this.loginSuccessHandler = new VaadinSavedRequestAwareAuthenticationSuccessHandler();
        this.logoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(
                clientRegistrationRepository);
        this.logoutSuccessHandler
                .setRedirectStrategy(new UidlRedirectStrategy());
        this.backChannelLogoutFilter = new BackChannelLogoutFilter(
                sessionRegistry, clientRegistrationRepository, eventPublisher);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

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

        http.oauth2Login(oauth2Login -> {
            // Sets Vaadin's login success handler that makes login redirects
            // compatible with Hilla endpoints. This is otherwise done
            // VaadinWebSecurity::setLoginView which is not used for OIDC.
            var requestCache = http.getSharedObject(RequestCache.class);
            if (requestCache != null) {
                loginSuccessHandler.setRequestCache(requestCache);
            }
            oauth2Login.successHandler(loginSuccessHandler);

            // Permit all requests to the login route.
            oauth2Login.loginPage(loginRoute).permitAll();

            // Sets the login route as endpoint for redirection when
            // trying to access a protected view without authorization.
            getViewAccessChecker().setLoginView(loginRoute);
        }).logout(logout -> {
            // Configures a logout success handler that takes care of closing
            // both the local user session and the OIDC provider remote session,
            // redirecting the web browser to the configured logout redirect
            // route when the process is completed.
            logoutSuccessHandler.setPostLogoutRedirectUri(logoutRedirectRoute);
            logout.logoutSuccessHandler(logoutSuccessHandler);
        }).exceptionHandling(exceptionHandling -> {
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
            http.csrf().ignoringRequestMatchers(matcher);
        }
    }
}
