/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */
import type { SingleSignOnData } from '../../../core/src/SingleSignOnData.js';

export const registeredProviders = ['keycloak'];

export const singleSignOnData: SingleSignOnData = {
  authenticated: false,
  roles: ['user'],
  loginLink: '/oauth2/authorization/keycloak',
  logoutLink: '/logout',
  backChannelLogoutEnabled: false
};

export function getRegisteredProviders(): Promise<string[]> {
  return Promise.resolve(registeredProviders);
}

export function fetchAll(): Promise<SingleSignOnData> {
  return Promise.resolve(singleSignOnData);
}
