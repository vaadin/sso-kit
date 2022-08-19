package com.vaadin.auth.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = VaadinAuthProperties.PREFIX)
public class VaadinAuthProperties {

    public static final String PREFIX = "vaadin.auth";

    private String loginRoute = "/login";

    public String getLoginRoute() {
        return loginRoute;
    }

    public void setLoginRoute(String loginRoute) {
        this.loginRoute = loginRoute;
    }
}
