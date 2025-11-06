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
const {resolve} = require("node:path");

// The current package
const cwd = process.cwd();

const isCI = !!process.env.CI;
const watch = !!process.argv.find((arg) => arg.includes('--watch')) && !isCI;
const coverage = !!process.argv.find((arg) => arg.includes('--coverage'));

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
