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
     * The default login route. Unauthorized requests will be redirected to this
     * route. If there are no views mapped to this route, Spring's default login
     * page will be generated.
     */
    static final String DEFAULT_LOGIN_ROUTE = "/login";

    /**
     * The default logout-redirect route. Web browsers will be redirected to
     * this route after the logout process has completed.
     */
    static final String DEFAULT_LOGOUT_REDIRECT_ROUTE = "/";

    /**
     * Enables (or disables) auto-configuration.
     */
    private boolean autoConfigure = true;

    /**
     * The route to redirect unauthorized requests to.
     */
    private String loginRoute = DEFAULT_LOGIN_ROUTE;

    /**
     * The route to redirect to after successful logout.
     */
    private String logoutRedirectRoute = DEFAULT_LOGOUT_REDIRECT_ROUTE;

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
     * Gets the logout-redirect route.
     *
     * @return the logout-redirect route
     */
    public String getLogoutRedirectRoute() {
        return logoutRedirectRoute;
    }

    /**
     * Sets the logout-redirect route.
     *
     * @param logoutRedirectRoute
     *            the logout-redirect route
     */
    public void setLogoutRedirectRoute(String logoutRedirectRoute) {
        this.logoutRedirectRoute = logoutRedirectRoute;
    }
}
