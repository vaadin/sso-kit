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

import dev.hilla.sso.starter.BackChannelLogoutSubscription;
import dev.hilla.sso.starter.SingleSignOnContext;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BackChannelLogoutEndpointTest {

    @Test
    public void serviceInit_indexHtmlRequestListenerIsAdded() {
        var singleSignOnContext = mock(SingleSignOnContext.class);
        var message = new BackChannelLogoutSubscription.Message();

        when(singleSignOnContext.getBackChannelLogoutFlux())
                .thenReturn(Flux.just(message));

        var backChannelLogoutEndpoint = new BackChannelLogoutEndpoint(
                singleSignOnContext);
        var messageFlux = backChannelLogoutEndpoint.subscribe();

        assertEquals(message, messageFlux.blockFirst());
    }
}
