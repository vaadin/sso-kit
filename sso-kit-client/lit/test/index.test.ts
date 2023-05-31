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
import { EndpointImportError } from "../src/index.js";

describe("@hilla/sso-kit-client-lit", () => {
  describe("Index", () => {
    it("should export EndpointImportError", async () => {
      expect(EndpointImportError).to.be.ok;
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
