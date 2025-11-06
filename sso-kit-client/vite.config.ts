/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */

import { readFile } from 'node:fs/promises';
import { resolve } from 'node:path';
// eslint-disable-next-line import/no-extraneous-dependencies
import { defineConfig } from 'vite';

// The current package
const cwd = process.cwd();

// https://vitejs.dev/config/
export default defineConfig(async () => {
  const [tsconfig] = await Promise.all([
    readFile(resolve(cwd, 'tsconfig.json'), 'utf8').then((f) => JSON.parse(f)),
  ]);

  return {
    build: {
      target: 'esnext',
    },
    esbuild: {
      tsconfigRaw: {
        ...tsconfig,
        compilerOptions: {
          ...tsconfig.compilerOptions,
          useDefineForClassFields: false,
        },
      },
    },
    resolve: {
      alias: {
        'Frontend/generated/SingleSignOnEndpoint.ts': resolve(cwd, 'test/mocks/SingleSignOnEndpointMock.ts'),
        'Frontend/generated/UserEndpoint.ts': resolve(cwd, 'test/mocks/UserEndpointMock.ts'),
        'Frontend/generated/BackChannelLogoutEndpoint.ts': resolve(cwd, 'test/mocks/BackChannelLogoutEndpointMock.ts')
      }
    }
  };
});
