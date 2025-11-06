/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */
package com.vaadin.hilla.sso.starter.endpoint;

import com.vaadin.hilla.sso.starter.BackChannelLogoutSubscription;
import com.vaadin.hilla.sso.starter.SingleSignOnContext;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BackChannelLogoutEndpointTest {

    @Test
    void subscribe_returnsTheExpectedFluxMessage() {
        var singleSignOnContext = mock(SingleSignOnContext.class);
        var message = new BackChannelLogoutSubscription.Message(
                "User logged out");

        when(singleSignOnContext.getBackChannelLogoutFlux())
                .thenReturn(Flux.just(message));

        var backChannelLogoutEndpoint = new BackChannelLogoutEndpoint(
                singleSignOnContext);
        var messageFlux = backChannelLogoutEndpoint.subscribe();

        assertEquals(message, messageFlux.blockFirst());
    }
}
