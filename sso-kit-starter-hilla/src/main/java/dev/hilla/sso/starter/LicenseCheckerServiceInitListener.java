/*-
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package dev.hilla.sso.starter;

import java.io.IOException;

import com.vaadin.flow.internal.UsageStatistics;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.pro.licensechecker.BuildType;
import com.vaadin.pro.licensechecker.LicenseChecker;

import static org.springframework.core.io.support.PropertiesLoaderUtils.loadAllProperties;

/**
 * Service initialization listener to verify the license.
 *
 * @author Vaadin Ltd
 */
public class LicenseCheckerServiceInitListener
        implements VaadinServiceInitListener {

    static final String PROPERTIES_RESOURCE = "sso-kit.properties";

    static final String VERSION_PROPERTY = "sso-kit.version";

    static final String PRODUCT_NAME = "vaadin-sso-kit-hilla";

    @Override
    public void serviceInit(ServiceInitEvent event) {
        final var service = event.getSource();

        try {
            final var properties = loadAllProperties(PROPERTIES_RESOURCE);
            final var version = properties.getProperty(VERSION_PROPERTY);

            UsageStatistics.markAsUsed(PRODUCT_NAME, version);

            // Check the license at runtime if in development mode
            if (!service.getDeploymentConfiguration().isProductionMode()) {
                // Using a null BuildType to allow trial licensing builds
                // The variable is defined to avoid method signature ambiguity
                BuildType buildType = null;
                LicenseChecker.checkLicense(PRODUCT_NAME, version, buildType);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
