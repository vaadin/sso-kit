/*-
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package dev.hilla.sso.starter.endpoint;

import java.util.List;

import com.vaadin.flow.server.auth.AnonymousAllowed;

import dev.hilla.Endpoint;
import dev.hilla.Nonnull;
import dev.hilla.sso.starter.SingleSignOnContext;
import dev.hilla.sso.starter.SingleSignOnData;

@Endpoint
@AnonymousAllowed
public class SingleSignOnEndpoint {

    private final SingleSignOnContext singleSignOnContext;

    public SingleSignOnEndpoint(SingleSignOnContext singleSignOnContext) {
        this.singleSignOnContext = singleSignOnContext;
    }

    @Nonnull
    public SingleSignOnData fetchAll() {
        return singleSignOnContext.getSingleSignOnData();
    }

    @Nonnull
    public List<@Nonnull String> getRegisteredProviders() {
        return singleSignOnContext.getRegisteredProviders();
    }
}
