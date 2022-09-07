package com.vaadin.auth.starter;

import javax.servlet.ServletException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.CompositeLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletResponse;
import com.vaadin.flow.spring.security.VaadinSavedRequestAwareAuthenticationSuccessHandler;
import com.vaadin.flow.spring.security.VaadinWebSecurity;

/**
 * This configuration bean is provided to auto-configure Vaadin and Spring to
 * allow single sign-on against external identity providers.
 * <p>
 * It enables OAuth2/OpenID login for the identity providers defined in the
 * current application configuration and instructs the application to accept
 * requests for the login route, which can be configured setting the
 * {@code vaadin.auth.login-route} property (defaults to {@code /login}).
 * <p>
 * If you need a customized security configuration, you can disable this
 * auto-configuration class by setting the {@code vaadin.auth.auto-configure}
 * property to {@code false} and provide your own configuration class.
 */
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "auto-configure", prefix = VaadinAuthProperties.PREFIX, matchIfMissing = true)
@EnableConfigurationProperties(VaadinAuthProperties.class)
public class VaadinAuthSecurityConfiguration extends VaadinWebSecurity {

    private final VaadinAuthProperties properties;

    private final OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler;

    private final VaadinSavedRequestAwareAuthenticationSuccessHandler loginSuccessHandler;

    private final DefaultVaadinAuthContext vaadinAuthContext;

    private final VaadinUserService vaadinUserService;

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
     */
    public VaadinAuthSecurityConfiguration(VaadinAuthProperties properties,
            SessionRegistry sessionRegistry,
            ClientRegistrationRepository clientRegistrationRepository) {
        this.properties = properties;
        this.sessionRegistry = sessionRegistry;
        this.loginSuccessHandler = new VaadinSavedRequestAwareAuthenticationSuccessHandler();
        this.logoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(
                clientRegistrationRepository);
        this.vaadinAuthContext = new DefaultVaadinAuthContext();
        this.vaadinUserService = new VaadinUserService();
        this.backChannelLogoutFilter = new BackChannelLogoutFilter(
                sessionRegistry, clientRegistrationRepository);
    }

    /*
     * Overriding this to intercept filter-chain build and set the configured
     * logout handlers in the authentication context.
     */
    @Bean(name = "VaadinSecurityFilterChainBean")
    @Override
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        configure(http);
        final var securityFilterChain = http.build();
        final var logoutConfigurer = http.logout();
        vaadinAuthContext.setLogoutHandlers(
                logoutConfigurer.getLogoutSuccessHandler(),
                logoutConfigurer.getLogoutHandlers());
        return securityFilterChain;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        final var loginRoute = Objects.requireNonNullElse(
                properties.getLoginRoute(),
                VaadinAuthProperties.DEFAULT_LOGIN_ROUTE);
        final var logoutRedirectRoute = Objects.requireNonNullElse(
                properties.getLogoutRedirectRoute(),
                VaadinAuthProperties.DEFAULT_LOGOUT_REDIRECT_ROUTE);
        final var backChannelLogoutRoute = Objects.requireNonNullElse(
                properties.getBackChannelLogoutRoute(),
                VaadinAuthProperties.DEFAULT_BACKCHANNEL_LOGOUT_ROUTE);
        final var maximumSessions = properties.getMaximumConcurrentSessions();

        http.oauth2Login(oauth2Login -> {
            // Sets Vaadin's custom user-service.
            oauth2Login.userInfoEndpoint().oidcUserService(vaadinUserService);

            // Sets Vaadin's login success handler that makes login redirects
            // compatible with Hilla endpoints. This is otherwise done
            // VaadinWebSecurity::setLoginView which it's not used for OIDC.
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
                final var expiredStrategy = new VaadinExpiredSessionStrategy();
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

    static class DefaultVaadinAuthContext implements VaadinAuthContext {

        private static final Logger LOGGER = LoggerFactory
                .getLogger(VaadinAuthContext.class);

        private LogoutSuccessHandler logoutSuccessHandler;

        private CompositeLogoutHandler logoutHandler;

        @Override
        public Optional<VaadinUser> getAuthenticatedUser() {
            return Optional.of(SecurityContextHolder.getContext())
                    .map(SecurityContext::getAuthentication)
                    .map(Authentication::getPrincipal)
                    .filter(VaadinUser.class::isInstance)
                    .map(VaadinUser.class::cast);
        }

        @Override
        public void logout() {
            final var req = VaadinServletRequest.getCurrent()
                    .getHttpServletRequest();
            final var res = VaadinServletResponse.getCurrent()
                    .getHttpServletResponse();
            final var auth = SecurityContextHolder.getContext()
                    .getAuthentication();

            logoutHandler.logout(req, res, auth);
            try {
                logoutSuccessHandler.onLogoutSuccess(req, res, auth);
            } catch (IOException | ServletException e) {
                // Raise a warning log message about the failure.
                LOGGER.warn("There was an error notifying the OIDC provider of "
                        + "the user logout", e);
            }
        }

        void setLogoutHandlers(LogoutSuccessHandler logoutSuccessHandler,
                List<LogoutHandler> logoutHandlers) {
            this.logoutSuccessHandler = logoutSuccessHandler;
            this.logoutHandler = new CompositeLogoutHandler(logoutHandlers);
        }

        /* For testing purposes */
        LogoutSuccessHandler getLogoutSuccessHandler() {
            return logoutSuccessHandler;
        }

        /* For testing purposes */
        CompositeLogoutHandler getLogoutHandler() {
            return logoutHandler;
        }
    }

    static class VaadinExpiredSessionStrategy
            implements SessionInformationExpiredStrategy {

        private static final Logger LOGGER = LoggerFactory
                .getLogger(VaadinExpiredSessionStrategy.class);

        @Override
        public void onExpiredSessionDetected(
                SessionInformationExpiredEvent event)
                throws IOException, ServletException {
            final var request = event.getRequest();
            final var response = event.getResponse();
            final var vaadinRequestParameter = request.getParameter("v-r");
            final var redirectRoute = '/' + request.getContextPath();
            if ("uidl".equals(vaadinRequestParameter)) {
                LOGGER.debug("Session expired during UIDL request: setting "
                        + "Vaadin-Refresh token");
                response.getWriter().write("Vaadin-Refresh: " + redirectRoute);
            } else {
                LOGGER.debug("Session expired: setting redirect status");
                response.setStatus(302);
                response.setHeader("Location", redirectRoute);
            }
        }
    }
}
