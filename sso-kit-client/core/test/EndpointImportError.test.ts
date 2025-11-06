/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */
import { expect } from '@esm-bundle/chai';
import { EndpointImportError } from '../src/index.js';

describe('@vaadin/sso-kit-client-core', () => {
  describe('EndpointImportError', () => {
    it('should be exported', async () => {
      expect(EndpointImportError).to.be.ok;
    });

    it('should instantiate with arguments', async () => {
      const endpointImportError = new EndpointImportError('endpoint', 'reason');
      expect(endpointImportError).to.be.instanceOf(EndpointImportError);
    });
  });
});
