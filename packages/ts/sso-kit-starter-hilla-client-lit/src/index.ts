export * from './SsoKit.js';

const $wnd = window as any;
/* c8 ignore next 2 */
$wnd.Vaadin = $wnd.Vaadin || {};
$wnd.Vaadin.registrations = $wnd.Vaadin.registrations || [];
$wnd.Vaadin.registrations.push({
    is: '@sso-kit-starter/sso-kit-starter-hilla-client-lit',
    version: /* updated-by-script */ '2.1-SNAPSHOT',
});
