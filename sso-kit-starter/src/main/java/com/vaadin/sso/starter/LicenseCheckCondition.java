/*-
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.sso.starter;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.vaadin.pro.licensechecker.BuildType;
import com.vaadin.pro.licensechecker.LicenseChecker;
import com.vaadin.pro.licensechecker.LicenseException;

class LicenseCheckCondition extends SpringBootCondition {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(LicenseCheckCondition.class);

    static final String PROPERTIES_RESOURCE = "sso-kit.properties";

    static final String VERSION_PROPERTY = "version";

    static final String PRODUCT_NAME = "vaadin-sso-kit";

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context,
            AnnotatedTypeMetadata metadata) {
        try {
            final var properties = getProperties();
            final var version = properties.getProperty(VERSION_PROPERTY);

            checkLicense(version);

            return ConditionOutcome.match();
        } catch (LicenseException e) {
            final var message = e.getMessage();
            return ConditionOutcome.noMatch(message);
        }
    }

    // Package protected for testing
    Properties getProperties() {
        try {
            return PropertiesLoaderUtils.loadAllProperties(PROPERTIES_RESOURCE);
        } catch (IOException e) {
            LOGGER.warn("Unable to read " + PROPERTIES_RESOURCE + " file", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    // Package protected for testing
    void checkLicense(String version) {
        LicenseChecker.checkLicense(PRODUCT_NAME, version,
                BuildType.PRODUCTION);
    }
}
