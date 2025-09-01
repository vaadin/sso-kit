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

import com.vaadin.flow.server.startup.BaseLicenseCheckerServiceInitListener;

/**
 * Service initialization listener to verify the license.
 *
 * @author Vaadin Ltd
 */
public class LicenseCheckerServiceInitListener
        extends BaseLicenseCheckerServiceInitListener {

    static final String PROPERTIES_RESOURCE = "sso-kit.properties";

    static final String VERSION_PROPERTY = "sso-kit.version";

    static final String PRODUCT_NAME = "vaadin-sso-kit";

    static final String PRODUCT_VERSION;

    static {
        final Properties properties = new Properties();
        try {
            properties.load(LicenseCheckerServiceInitListener.class
                    .getClassLoader().getResourceAsStream(PROPERTIES_RESOURCE));
            PRODUCT_VERSION = properties.getProperty(VERSION_PROPERTY);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Initialises a license checking mechanism for SSO Kit using it's product
     * name and current version.
     */
    public LicenseCheckerServiceInitListener() {
        super(PRODUCT_NAME, PRODUCT_VERSION);
    }
}
