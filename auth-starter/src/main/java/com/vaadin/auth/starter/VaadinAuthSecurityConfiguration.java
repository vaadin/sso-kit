package com.vaadin.auth.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.util.StringUtils;

import com.vaadin.flow.server.auth.ViewAccessChecker;
import com.vaadin.flow.spring.security.VaadinWebSecurity;

@Configuration
@EnableWebSecurity
@ConditionalOnMissingBean(VaadinWebSecurity.class)
@EnableConfigurationProperties(VaadinAuthProperties.class)
public class VaadinAuthSecurityConfiguration extends VaadinWebSecurity {

    private final VaadinAuthProperties properties;

    private final ViewAccessChecker viewAccessChecker;

    public VaadinAuthSecurityConfiguration(VaadinAuthProperties properties,
            ViewAccessChecker viewAccessChecker) {
        this.properties = properties;
        this.viewAccessChecker = viewAccessChecker;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        OAuth2LoginConfigurer<HttpSecurity> oauth2Login = http.oauth2Login();

        final var loginRoute = properties.getLoginRoute();
        if (StringUtils.hasLength(loginRoute)) {
            // Permit all requests to the login route
            oauth2Login.loginPage(loginRoute).permitAll();

            // Sets the login route as endpoint for redirection when trying to
            // access a protected view without authorization
            viewAccessChecker.setLoginView(loginRoute);
        }
    }
}
