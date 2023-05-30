/*-
 * Copyright (C) 2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
import type { SingleSignOnData } from '@hilla/sso-kit-client-core/index.js';
import { SingleSignOnContext } from './SingleSignOnContext.js';

export * from './SingleSignOnContext.js';
export * from './RequireAuth.js';
export { EndpointImportError } from '@hilla/sso-kit-client-core/EndpointImportError.js';
export type { AccessProps } from '@hilla/sso-kit-client-core/AccessProps.js';
export type { User } from '@hilla/sso-kit-client-core/User.js';

declare global {
  interface Window {
    Vaadin: {
      SingleSignOnData?: SingleSignOnData;
      registrations?: {
        is: string;
        version: string;
      }[];
    };
  }
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.registrations = window.Vaadin.registrations || [];
window.Vaadin.registrations.push({
  is: '@hilla/sso-kit-client-react',
  version: '2.1.0',
});

window.Vaadin.SingleSignOnData = window.Vaadin.SingleSignOnData || {
  authenticated: false,
  roles: [],
  loginLink: '',
  logoutLink: undefined,
  backChannelLogoutEnabled: false,
};

const ssoContext = new SingleSignOnContext(window.Vaadin.SingleSignOnData);
export default ssoContext;
