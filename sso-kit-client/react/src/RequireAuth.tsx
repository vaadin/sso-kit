/*-
 * Copyright (C) 2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
import type { ReactElement } from "react";
import ssoContext from "./index.js";

export function RequireAuth({ children }: { children: ReactElement }) {
  if (!ssoContext.authenticated) {
    window.location.href = ssoContext.loginUrl!;
  }
  return children;
}
