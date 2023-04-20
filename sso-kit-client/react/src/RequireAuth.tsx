/*-
 * Copyright (C) 2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
import { Navigate, useLocation } from "react-router-dom";
import type { ReactElement } from "react";

export function RequireAuth({ children }: { children: ReactElement }) {
  if (!window.Vaadin.SingleSignOnData?.authenticated) {
    // window.location.href = ssoContext.loginUrl!;
    const location = useLocation();
    return <Navigate to="/" state={{ from: location }} replace />;
  }
  return children;
}
