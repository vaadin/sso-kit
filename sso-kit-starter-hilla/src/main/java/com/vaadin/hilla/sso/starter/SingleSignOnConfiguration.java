/*-
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.hilla.sso.starter;

import com.vaadin.hilla.sso.starter.endpoint.BackChannelLogoutEndpoint;
import com.vaadin.hilla.sso.starter.endpoint.SingleSignOnEndpoint;
import com.vaadin.hilla.sso.starter.endpoint.UserEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.ClientsConfiguredCondition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import com.vaadin.sso.core.BackChannelLogoutFilter;

/**
 * This configuration bean is provided to auto-configure Hilla and Spring to
 * allow single sign-on against external identity providers.
 * <p>
 * It enables OAuth2/OpenID login for the identity providers defined in the
 * current application configuration and instructs the application to accept
 * requests for the login route, which can be configured setting the
 * {@code hilla.sso.login-route} property (defaults to {@code /login}).
 * <p>
 * If you need a customized security configuration, you can disable this
 * auto-configuration class by adding its fully-qualified name to the
 * {@code spring.autoconfigure.exclude} property and provide your own
 * configuration class.
 *
 * @author Vaadin Ltd
 * @since 2.0
 */
@AutoConfiguration
@EnableWebSecurity
@Conditional(ClientsConfiguredCondition.class)
@EnableConfigurationProperties(SingleSignOnProperties.class)
public class SingleSignOnConfiguration extends VaadinWebSecurity {

    private final SingleSignOnProperties properties;

    private final BackChannelLogoutFilter backChannelLogoutFilter;

    private final SessionRegistry sessionRegistry;

    private final BackChannelLogoutSubscription backChannelLogoutSubscription;

    private final SingleSignOnContext singleSignOnContext;

    private final BootstrapDataServiceListener bootstrapDataServiceListener;

    private final BackChannelLogoutEndpoint backChannelLogoutEndpoint;

    private final SingleSignOnEndpoint singleSignOnEndpoint;

    private final UserEndpoint userEndpoint;

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
     *            the event publisher for logout events
     */
    public SingleSignOnConfiguration(SingleSignOnProperties properties,
            SessionRegistry sessionRegistry,
            ClientRegistrationRepository clientRegistrationRepository,
            ApplicationEventPublisher eventPublisher) {
        this.properties = properties;
        this.sessionRegistry = sessionRegistry;
        this.backChannelLogoutFilter = new BackChannelLogoutFilter(
                sessionRegistry, clientRegistrationRepository, eventPublisher);
        this.backChannelLogoutSubscription = new BackChannelLogoutSubscription();
        this.singleSignOnContext = new SingleSignOnContext(
                clientRegistrationRepository, properties,
                backChannelLogoutSubscription);
        this.bootstrapDataServiceListener = new BootstrapDataServiceListener(
                singleSignOnContext);
        this.backChannelLogoutEndpoint = new BackChannelLogoutEndpoint(
                singleSignOnContext);
        this.singleSignOnEndpoint = new SingleSignOnEndpoint(
                singleSignOnContext);
        this.userEndpoint = new UserEndpoint();
    }

    @Bean
    public BackChannelLogoutSubscription backChannelLogoutSubscription() {
        return backChannelLogoutSubscription;
    }

    @Bean
    public SingleSignOnContext singleSignOnContext() {
        return singleSignOnContext;
    }

    @Bean
    public BootstrapDataServiceListener bootstrapDataServiceListener() {
        return bootstrapDataServiceListener;
    }

    @Bean
    public BackChannelLogoutEndpoint backChannelLogoutEndpoint() {
        return backChannelLogoutEndpoint;
    }

    @Bean
    public SingleSignOnEndpoint singleSignOnEndpoint() {
        return singleSignOnEndpoint;
    }

    @Bean
    public UserEndpoint userEndpoint() {
        return userEndpoint;
    }

    @Bean(name = "VaadinSecurityFilterChainBean")
    @Override
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.oauth2Login()
                // Set the default login route
                .loginPage(properties.getLoginRoute())
                // Set a default logout success route, as it is required,
                // although it is not used
                .and().logout().logoutSuccessUrl("/")
                // Setup session management
                .and().sessionManagement().sessionConcurrency(concurrency -> {
                    // Sets the maximum number of concurrent sessions per user.
                    // The default is -1 which means no limit on the number of
                    // concurrent sessions per user.
                    concurrency.maximumSessions(
                            properties.getMaximumConcurrentSessions());
                    // Sets the session-registry which is used for Back-Channel
                    concurrency.sessionRegistry(sessionRegistry);
                });

        if (properties.isBackChannelLogout()) {
            backChannelLogoutFilter.setBackChannelLogoutRoute(
                    properties.getBackChannelLogoutRoute());

            // Adds the Back-Channel logout filter to the filter chain
            http.addFilterAfter(backChannelLogoutFilter, LogoutFilter.class);

            // Disable CSRF for Back-Channel logout requests
            final var matcher = backChannelLogoutFilter.getRequestMatcher();
            http.csrf().ignoringRequestMatchers(matcher);
        } else {
            http.oidcLogout().backChannel(Customizer.withDefaults());
        }

        return http.build();
    }
}
