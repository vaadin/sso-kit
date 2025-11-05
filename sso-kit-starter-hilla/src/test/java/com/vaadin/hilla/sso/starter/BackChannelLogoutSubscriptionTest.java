/*-
 * Copyright (C) 2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.hilla.sso.starter;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vaadin.sso.core.UserLogoutEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Test suite for {@link BackChannelLogoutSubscription}.
 */
@ExtendWith(MockitoExtension.class)
class BackChannelLogoutSubscriptionTest {

    @Test
    void onApplicationEvent_broadcastsEvent() {
        var backChannelLogoutSubscription = spy(
                new BackChannelLogoutSubscription());
        var backChannelLogoutEvent = new UserLogoutEvent("foo");
        backChannelLogoutSubscription
                .onApplicationEvent(backChannelLogoutEvent);
        verify(backChannelLogoutSubscription).broadcast("foo");
    }

    @Test
    void getFluxForUser_returnsMessageFlux() {
        var backChannelLogoutSubscription = new BackChannelLogoutSubscription();
        var flux = backChannelLogoutSubscription.getFluxForUser("foo");
        assertNotNull(flux);
        var received = new ArrayList<>();
        flux.subscribe(received::add);
        backChannelLogoutSubscription.broadcast("bar");
        backChannelLogoutSubscription.broadcast("foo");
        backChannelLogoutSubscription.broadcast("bar");
        backChannelLogoutSubscription.broadcast("foo");
        backChannelLogoutSubscription.broadcast("bar");
        assertEquals(2, received.size());
        assertInstanceOf(BackChannelLogoutSubscription.Message.class,
                received.get(0));
        assertInstanceOf(BackChannelLogoutSubscription.Message.class,
                received.get(1));
    }
}
