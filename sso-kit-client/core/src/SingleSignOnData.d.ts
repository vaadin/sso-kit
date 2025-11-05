/*-
 * Copyright (C) 2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
/**
 * Type definition for the authentication information.
 */
export type SingleSignOnData = Readonly<{
  authenticated: boolean;
  roles: string[];
  loginLink: string;
  logoutLink?: string;
  backChannelLogoutEnabled: boolean;
}>;
