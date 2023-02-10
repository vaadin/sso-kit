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

import java.util.ArrayList;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.vaadin.sso.core.UserLogoutEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Test suite for {@link BackChannelLogoutSubscription}.
 */
@ExtendWith(MockitoExtension.class)
public class BackChannelLogoutSubscriptionTest {

    @Mock
    private Consumer<Object> consumer;

    /**
     * Test BackChannelLogoutSubscription constructor
     */
    @Test
    public void backChannelLogoutSubscriptionConstructor_createsFlux() {
        var backChannelLogoutSubscription = new BackChannelLogoutSubscription();
        assertNotNull(ReflectionTestUtils
                .getField(backChannelLogoutSubscription, "flux"));
    }

    @Test
    public void onApplicationEvent_broadcastsEvent() {
        var backChannelLogoutSubscription = spy(
                new BackChannelLogoutSubscription());
        var backChannelLogoutEvent = new UserLogoutEvent("foo");
        backChannelLogoutSubscription
                .onApplicationEvent(backChannelLogoutEvent);
        verify(backChannelLogoutSubscription).broadcast("foo");
    }

    @Test
    public void getFluxForUser_returnsFLux() {
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
    }

    @Test
    public void broadcasts_anEvent() {
        var backChannelLogoutSubscription = new BackChannelLogoutSubscription();
        ReflectionTestUtils.setField(backChannelLogoutSubscription, "consumer",
                consumer);
        backChannelLogoutSubscription.broadcast("foo");
        verify(consumer).accept("foo");
    }
}
