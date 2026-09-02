/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */
import type { Subscription } from '@vaadin/hilla-frontend';

type Message = {
  message?: string;
};

/**
 * A subscription that never emits, enough for the code under test to attach
 * its callbacks to.
 */
export function subscribe(): Subscription<Message> {
  const subscription = {
    cancel: () => {
      /* do nothing */
    },
    context: () => subscription,
    onComplete: () => subscription,
    onConnectionStateChange: () => subscription,
    onError: () => subscription,
    onNext: () => subscription,
    onSubscriptionLost: () => subscription
  };
  return subscription as unknown as Subscription<Message>;
}
