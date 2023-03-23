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
        'Frontend/generated/SingleSignOnEndpoint.js': resolve(cwd, 'test/mocks/SingleSignOnEndpointMock.ts'),
        'Frontend/generated/UserEndpoint.js': resolve(cwd, 'test/mocks/UserEndpointMock.ts'),
        'Frontend/generated/BackChannelLogoutEndpoint.js': resolve(cwd, 'test/mocks/BackChannelLogoutEndpointMock.ts')
      }
    }
  };
});
