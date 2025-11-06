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
 */
@ConfigurationProperties(prefix = SingleSignOnProperties.PREFIX)
public class SingleSignOnProperties extends AbstractSingleSignOnProperties {

    /**
     * The prefix for SSO Kit starter properties.
     */
    public static final String PREFIX = "hilla.sso";
}
