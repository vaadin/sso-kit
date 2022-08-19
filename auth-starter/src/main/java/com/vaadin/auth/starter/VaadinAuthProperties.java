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

    private String loginRoute = "/login";

    /**
     * Gets the login-route property.
     *
     * @return the login-route property
     */
    public String getLoginRoute() {
        return loginRoute;
    }

    /**
     * Set the login-route property.
     *
     * @param loginRoute
     *            the login-route property
     */
    public void setLoginRoute(String loginRoute) {
        this.loginRoute = loginRoute;
    }
}
