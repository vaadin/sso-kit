package com.vaadin.auth.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.util.StringUtils;

import com.vaadin.flow.server.auth.ViewAccessChecker;
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

    private final ViewAccessChecker viewAccessChecker;

    /**
     * Creates an instance of this configuration bean.
     *
     * @param properties
     *            the configuration properties
     * @param viewAccessChecker
     *            the view-access-checker
     */
    public VaadinAuthSecurityConfiguration(VaadinAuthProperties properties,
            ViewAccessChecker viewAccessChecker) {
        this.properties = properties;
        this.viewAccessChecker = viewAccessChecker;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        OAuth2LoginConfigurer<HttpSecurity> oauth2Login = http.oauth2Login();

        final String loginRoute = properties.getLoginRoute();
        if (StringUtils.hasLength(loginRoute)) {
            // Permit all requests to the login route
            oauth2Login.loginPage(loginRoute).permitAll();

            // Sets the login route as endpoint for redirection when trying to
            // access a protected view without authorization
            viewAccessChecker.setLoginView(loginRoute);
        }
    }
}
