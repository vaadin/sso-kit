/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */
import type { User } from '../../../core/src/User.js';

export const authenticatedUser: User = {
  fullName: 'John Doe',
  preferredUsername: 'john'
};

export function getAuthenticatedUser(): Promise<User> {
  return Promise.resolve(authenticatedUser);
}
