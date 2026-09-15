/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */

const karmaChromeLauncher = require('karma-chrome-launcher');
const karmaCoverage = require('karma-coverage');
const karmaMocha = require('karma-mocha');
const karmaVite = require('karma-vite');
const {readFileSync} = require('node:fs');
const {resolve} = require("node:path");

// The current package
const cwd = process.cwd();

const isCI = !!process.env.CI;
const watch = !!process.argv.find((arg) => arg.includes('--watch')) && !isCI;
const coverage = !!process.argv.find((arg) => arg.includes('--coverage'));

const tsconfig = JSON.parse(readFileSync(resolve(cwd, 'tsconfig.json'), 'utf8'));

module.exports = (config) => {
  config.set({
    plugins: [
      karmaMocha,
      karmaChromeLauncher,
      karmaVite,
      karmaCoverage,
    ],

    browsers: ['ChromeHeadlessNoSandbox'],

    customLaunchers: {
      ChromeHeadlessNoSandbox: {
        base: 'ChromeHeadless',
        flags: ['--no-sandbox', '--disable-setuid-sandbox'],
      },
    },

    frameworks: ['vite', 'mocha'],

    // The packages run their test suites in parallel, each one starting its own
    // Vite dev server from its own working directory. The Vite configuration is
    // therefore inlined here and `configFile` is disabled on purpose: loading a
    // shared `vite.config.ts` makes every server bundle it into a temp file
    // named after the current millisecond, so the servers delete each other's
    // temp file whenever two of them start within the same millisecond. For the
    // same reason `cacheDir` is pinned to the current package: the Vite root is
    // the Karma `basePath`, which is shared by all three packages, and the
    // dependency optimizer would otherwise wipe the shared cache directory
    // while a sibling server is still serving files out of it.
    vite: {
      config: {
        configFile: false,
        cacheDir: resolve(cwd, 'node_modules/.vite'),
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
      }
    },

    files: [
      {
        pattern: resolve(cwd, 'test/**/*.test.ts'),
        type: 'module',
        watched: false,
        served: false,
      },
    ],

    reporters: ['progress', coverage && 'coverage'].filter(Boolean),

    autoWatch: watch,
    singleRun: !watch,

    coverageReporter: {
      dir: '.coverage/',
      reporters: [
        !isCI && {type: 'html', subdir: 'html'},
        {type: 'lcovonly', subdir: '.'},
      ].filter(Boolean),
    },
  });
};
