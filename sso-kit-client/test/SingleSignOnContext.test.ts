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
import { SingleSignOnContext } from "../src/index.js";

describe("@hilla/sso-kit-client", () => {
  describe("SingleSignOnContext", () => {
    beforeEach(() => {
      window.Vaadin = {
        SingleSignOnData: {
          authenticated: false,
          roles: [],
          loginLink: "/oauth2/authorization/keycloak",
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
      expect(SingleSignOnContext).to.be.ok;
    });

    it("should instantiate with argument", async () => {
      const singleSignOnContext = new SingleSignOnContext(
        window.Vaadin.SingleSignOnData!
      );
      expect(singleSignOnContext).to.be.instanceOf(SingleSignOnContext);
    });
  });
});
