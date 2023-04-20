/*-
 * Copyright (C) 2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
import type { SingleSignOnData } from "../../core/src/SingleSignOnData.js";

export * from "./SingleSignOnContext.js";
export * from "./RequireAuth.js";
export { EndpointImportError } from "../../core/src/EndpointImportError.js";
export type { AccessProps } from "../../core/src/AccessProps.js";
export type { User } from "../../core/src/User.js";

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
  is: "@hilla/sso-kit-client-react",
  version: "2.1.0",
});
