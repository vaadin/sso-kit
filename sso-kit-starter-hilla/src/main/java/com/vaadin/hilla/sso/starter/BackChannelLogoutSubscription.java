/*-
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.hilla.sso.starter;

import java.util.Objects;
import java.util.function.Consumer;

import org.springframework.context.ApplicationListener;
import reactor.core.publisher.Flux;

import com.vaadin.sso.core.UserLogoutEvent;

/**
 * A subscription for broadcasting back-channel logout events to the subscribed
 * clients.
 */
public class BackChannelLogoutSubscription
        implements ApplicationListener<UserLogoutEvent> {

    private Consumer<Object> consumer;

    private final Flux<Object> flux;

    public BackChannelLogoutSubscription() {
        // Create a Flux and prepare to store the FluxSink "next" method as a
        // consumer, that will be called when a user is logged out.
        flux = Flux.create(sink -> consumer = sink::next).share();
    }

    /**
     * When an {@link UserLogoutEvent} is received, broadcast the event to all
     * subscribed clients.
     *
     * @param event
     *            the event to broadcast.
     */
    @Override
    public void onApplicationEvent(UserLogoutEvent event) {
        broadcast(event.getSource());
    }

    /**
     * Create a filtered Flux for the given user.
     *
     * @param principal
     *            the user to filter the Flux for.
     * @return a Flux that only concerns the given user.
     */
    public Flux<Message> getFluxForUser(Object principal) {
        Objects.requireNonNull(principal);
        return flux.filter(p -> Objects.equals(p, principal))
                .map(p -> new Message("User logged out"));
    }

    /**
     * Broadcast a back-channel logout event
     *
     * @param principal
     *            the user that accepts the logout event.
     * @return true if broadcast succeeds, false otherwise.
     */
    public boolean broadcast(Object principal) {
        if (consumer != null) {
            consumer.accept(principal);
            return true;
        }
        return false;
    }

    /**
     * The message returned by the flux.
     */
    public record Message(String message) {
    }
}
