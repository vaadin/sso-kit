/*-
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
export * from './SingleSignOnContext.js';

declare global {
  interface Window {
    Vaadin: {
      registrations: {
        is: string;
        version: string;
      }[];
    }
  }
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.registrations = window.Vaadin.registrations || [];
window.Vaadin.registrations.push({
  is: "@hilla/sso-kit-client-lit",
  version: /* updated-by-script */ "2.1.0"
});
