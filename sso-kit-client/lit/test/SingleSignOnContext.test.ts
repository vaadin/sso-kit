/*-
 * Copyright (C) 2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
import { expect } from "@esm-bundle/chai";
import { SingleSignOnContext } from "../src/index.js";

describe("@hilla/sso-kit-client-lit", () => {
  describe("SingleSignOnContext", () => {
    it("should be exported", async () => {
      expect(SingleSignOnContext).to.be.ok;
    });
  });
});
