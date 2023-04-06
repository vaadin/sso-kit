/*-
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
import { expect } from "chai";
import EndpointImportError from "../src/EndpointImportError.js";

describe("@hilla/sso-kit-client", () => {
  describe("EndpointImportError", () => {
    beforeEach(() => {
      window.Vaadin = {
        SingleSignOnData: {
          authenticated: false,
          roles: [],
          loginLink: "/oauth2/authorization/keycloak",
          logoutLink: "/logout",
          backChannelLogoutEnabled: false,
        },
        registrations: [
          {
            is: "@hilla/sso-kit-client",
            version: "2.1.0",
          },
        ],
      };
    });

    it("should be exported", async () => {
      expect(EndpointImportError).to.be.ok;
    });

    it("should instantiate with arguments", async () => {
      const endpointImportError = new EndpointImportError("endpoint", "reason");
      expect(endpointImportError).to.be.instanceOf(EndpointImportError);
    });
  });
});
