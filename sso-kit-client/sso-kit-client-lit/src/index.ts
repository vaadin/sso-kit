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

const $wnd = window as any;
/* c8 ignore next 2 */
$wnd.Vaadin = $wnd.Vaadin || {};
$wnd.Vaadin.registrations = $wnd.Vaadin.registrations || [];
$wnd.Vaadin.registrations.push({
    is: "@hilla/sso-kit-client-lit",
    version: /* updated-by-script */ "2.1.0"
});
