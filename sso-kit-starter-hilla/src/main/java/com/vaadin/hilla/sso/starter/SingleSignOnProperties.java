/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */
package com.vaadin.hilla.sso.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.vaadin.sso.core.AbstractSingleSignOnProperties;

/**
 * Definition of configuration properties for the SSO Kit starter.
 *
 * @author Vaadin Ltd
 * @since 1.0
 *
 * @deprecated SSO Kit is deprecated as of Vaadin 25.3 and will be removed in
 *             Vaadin 26.0. Use the built-in Spring Security support for OpenID
 *             Connect instead.
 */
@ConfigurationProperties(prefix = SingleSignOnProperties.PREFIX)
@Deprecated(since = "4.2", forRemoval = true)
public class SingleSignOnProperties extends AbstractSingleSignOnProperties {

    /**
     * The prefix for SSO Kit starter properties.
     */
    public static final String PREFIX = "hilla.sso";
}
