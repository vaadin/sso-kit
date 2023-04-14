/*-
 * Copyright (C) 2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
import { assert } from "@esm-bundle/chai";

describe("@hilla/sso-kit-client-lit", () => {
  describe("Index", () => {
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
            is: "@hilla/sso-kit-client-lit",
            version: "2.1.0",
          },
        ],
      };
    });

    it("should add registration", async () => {
      assert.isDefined(window.Vaadin);
      assert.isArray(window.Vaadin.registrations);
      const formRegistrations = window.Vaadin.registrations?.filter(
        (r: any) => r.is === "@hilla/sso-kit-client-lit"
      );
      assert.lengthOf(formRegistrations!, 1);
    });
  });
});
