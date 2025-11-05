/*-
 * Copyright (C) 2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
import { expect } from "@esm-bundle/chai";
import { EndpointImportError } from "../src/index.js";

describe("@vaadin/sso-kit-client-core", () => {
  describe("Index", () => {
    it("should export EndpointImportError", async () => {
      expect(EndpointImportError).to.be.ok;
    });
  });
});
