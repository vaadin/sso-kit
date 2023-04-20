/*-
 * Copyright (C) 2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
import { assert, expect } from "@esm-bundle/chai";
import ssoContext, { SingleSignOnContext } from "../src/index.js";

describe("@hilla/sso-kit-client-react", () => {
  describe("Index", () => {
    it("should add registration", async () => {
      assert.isDefined(window.Vaadin);
      assert.isArray(window.Vaadin.registrations);
      const formRegistrations = window.Vaadin.registrations?.filter(
        (r: any) => r.is === "@hilla/sso-kit-client-react"
      );
      assert.lengthOf(formRegistrations!, 1);
    });

    it("should instantiate SingleSignOnContext with argument", async () => {
      assert.isDefined(window.Vaadin);
      assert.isObject(window.Vaadin.SingleSignOnData);
      expect(ssoContext).to.be.instanceOf(SingleSignOnContext);
    });
  });
});
