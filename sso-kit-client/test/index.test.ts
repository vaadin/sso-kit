/*-
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
import { assert } from "@esm-bundle/chai";

describe("@hilla/sso-kit-client", () => {
  describe("Index", () => {
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

    it("should add registration", async () => {
      assert.isDefined(window.Vaadin);
      assert.isArray(window.Vaadin.registrations);
      const formRegistrations = window.Vaadin.registrations?.filter(
        (r: any) => r.is === "@hilla/sso-kit-client"
      );
      assert.lengthOf(formRegistrations!, 1);
    });
  });
});
