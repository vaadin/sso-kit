package com.vaadin.auth.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Definition of configuration properties for the Vaadin Auth starter.
 */
@ConfigurationProperties(prefix = VaadinAuthProperties.PREFIX)
public class VaadinAuthProperties {

    /**
     * The prefix for Vaadin Auth starter properties.
     */
    public static final String PREFIX = "vaadin.auth";

    /**
     * The default login-route.
     */
    public static final String DEFAULT_LOGIN_ROUTE = "/login";

    /**
     * The default logout-redirect-url.
     */
    public static final String DEFAULT_LOGOUT_REDIRECT_URL = "/";

    private boolean autoConfigure = true;

    private String loginRoute = DEFAULT_LOGIN_ROUTE;

    private String logoutRedirectUrl = DEFAULT_LOGOUT_REDIRECT_URL;

    /**
     * Checks is auto-configuration of {@link VaadinAuthSecurityConfiguration}
     * is enabled.
     *
     * @return true, if auto-configuration is enabled
     */
    public boolean isAutoConfigure() {
        return autoConfigure;
    }

    /**
     * Enables or disables auto-configuration of
     * {@link VaadinAuthSecurityConfiguration}.
     *
     * @param autoConfigure
     *            {@code true} to enable auto-configuration, {@code false} to
     *            disable
     */
    public void setAutoConfigure(boolean autoConfigure) {
        this.autoConfigure = autoConfigure;
    }

    /**
     * Gets the login-route property.
     *
     * @return the login-route property
     */
    public String getLoginRoute() {
        return loginRoute;
    }

    /**
     * Sets the login-route property.
     *
     * @param loginRoute
     *            the login-route property
     */
    public void setLoginRoute(String loginRoute) {
        this.loginRoute = loginRoute;
    }

    /**
     * Gets the logout-redirect-url property.
     *
     * @return the logout-redirect-url property
     */
    public String getLogoutRedirectUrl() {
        return logoutRedirectUrl;
    }

    /**
     * Sets the logout-redirect-url property.
     *
     * @param logoutRedirectUrl
     *            the logout-redirect-url property
     */
    public void setLogoutRedirectUrl(String logoutRedirectUrl) {
        this.logoutRedirectUrl = logoutRedirectUrl;
    }
}
