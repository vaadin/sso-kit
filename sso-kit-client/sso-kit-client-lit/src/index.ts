export * from "./SingleSignOnContext.js";

const $wnd = window as any;
/* c8 ignore next 2 */
$wnd.Vaadin = $wnd.Vaadin || {};
$wnd.Vaadin.registrations = $wnd.Vaadin.registrations || [];
$wnd.Vaadin.registrations.push({
    is: "@sso-kit/sso-kit-client-lit",
    version: /* updated-by-script */ "2.0-SNAPSHOT"
});
