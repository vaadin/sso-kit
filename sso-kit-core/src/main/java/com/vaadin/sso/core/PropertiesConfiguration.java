/*-
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.sso.core;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration of Flow and Hilla SSO Kit starter configuration properties.
 *
 * @author Vaadin Ltd
 * @since 1.0
 */
@Configuration
public class PropertiesConfiguration {

    /**
     * The prefix for Flow SSO Kit starter properties.
     */
    public static final String VAADIN_SSO_PREFIX = "vaadin.sso";

    /**
     * The prefix for Hilla SSO Kit starter properties.
     */
    public static final String HILLA_SSO_PREFIX = "hilla.sso";

    /**
     * Definition of configuration properties for the Flow SSO Kit starter.
     */
    @Bean
    @ConfigurationProperties(prefix = VAADIN_SSO_PREFIX)
    @ConditionalOnClass(name = "com.vaadin.sso.starter.UidlRedirectStrategy")
    public SingleSignOnProperties vaadinSingleSignOnProperties() {
        return new SingleSignOnProperties();
    }

    /**
     * Definition of configuration properties for the Hilla SSO Kit starter.
     */
    @Bean
    @ConfigurationProperties(prefix = HILLA_SSO_PREFIX)
    @ConditionalOnClass(name = "dev.hilla.sso.starter.SingleSignOnContext")
    public SingleSignOnProperties hillaSingleSignOnProperties() {
        return new SingleSignOnProperties();
    }
}
