/*-
 * Copyright (C) 2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
/**
 * The error thrown when an endpoint import fails because of error or not found.
 */
export class EndpointImportError extends Error {
  public constructor(endpoint: string, reason: string) {
    super(`${endpoint} endpoint import failed with error: ${reason}`);
  }
}
