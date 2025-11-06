/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */
package com.vaadin.hilla.sso.starter.endpoint;

import java.util.List;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.Endpoint;
import com.vaadin.hilla.Nonnull;
import com.vaadin.hilla.sso.starter.SingleSignOnContext;
import com.vaadin.hilla.sso.starter.SingleSignOnData;

/**
 * Endpoint for fetching the SSO data
 */
@Endpoint
@AnonymousAllowed
public class SingleSignOnEndpoint {

    private final SingleSignOnContext singleSignOnContext;

    public SingleSignOnEndpoint(SingleSignOnContext singleSignOnContext) {
        this.singleSignOnContext = singleSignOnContext;
    }

    /**
     * Fetches the essential SSO data in a single call
     *
     * @return the SSO data
     */
    @Nonnull
    public SingleSignOnData fetchAll() {
        return singleSignOnContext.getSingleSignOnData();
    }

    /**
     * Gets a list of the registered SSO providers
     *
     * @return a list of the registered SSO providers, as defined in the
     *         application properties
     */
    @Nonnull
    public List<@Nonnull String> getRegisteredProviders() {
        return singleSignOnContext.getRegisteredProviders();
    }
}
