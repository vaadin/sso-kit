/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */
import { expect } from '@esm-bundle/chai';
import { createElement } from 'react';
import type { Root } from 'react-dom/client';
import { createRoot } from 'react-dom/client';
import type { RouteObject } from 'react-router';
import { Outlet, RouterProvider, createMemoryRouter } from 'react-router';
import { protectRoutes, SsoProvider } from '../src/index.js';
import type { AccessProps } from '../../core/src/AccessProps.js';
import { singleSignOnData } from './mocks/SingleSignOnEndpointMock.js';

type TestRoute = RouteObject & AccessProps;

/**
 * A hash link, so that the login route can navigate without leaving the page
 * the tests are running on.
 */
const loginLink = '#login-link';

function view(id: string) {
  return createElement('div', { id }, id);
}

function waitFor(condition: () => boolean, describeFailure: () => string, attempts = 100): Promise<void> {
  if (condition()) {
    return Promise.resolve();
  }
  if (attempts === 0) {
    return Promise.reject(new Error(describeFailure()));
  }
  return new Promise((resolve) => setTimeout(resolve, 20)).then(() =>
    waitFor(condition, describeFailure, attempts - 1)
  );
}

describe('@vaadin/sso-kit-client-react', () => {
  describe('ProtectedRoute', () => {
    let container: HTMLElement;
    let root: Root;

    function protectedRoutes(redirectPath?: string): TestRoute[] {
      const routes: TestRoute[] = [
        { path: '/profile', element: view('profile'), requireAuthentication: true },
        { path: '/login', element: view('login') }
      ];
      return protectRoutes(routes, redirectPath);
    }

    /**
     * Renders the given routes starting from the 'profile' route, which
     * requires authentication.
     */
    function render(routes: TestRoute[], authenticated: boolean): void {
      window.Vaadin.SingleSignOnData = { ...singleSignOnData, authenticated, loginLink };
      const router = createMemoryRouter(
        [{ path: '/', element: createElement(SsoProvider, null, createElement(Outlet)), children: routes }],
        { initialEntries: ['/profile'] }
      );
      root.render(createElement(RouterProvider, { router }));
    }

    async function waitForView(id: string): Promise<void> {
      await waitFor(
        () => !!container.querySelector(`#${id}`),
        () => `Expected '${id}' to be rendered, got '${container.innerHTML}'`
      );
    }

    beforeEach(() => {
      container = document.createElement('div');
      document.body.appendChild(container);
      root = createRoot(container);
    });

    afterEach(() => {
      root.unmount();
      container.remove();
      window.Vaadin.SingleSignOnData = undefined;
      window.location.hash = '';
    });

    it('should add the login route to the protected routes', async () => {
      expect(protectedRoutes().map((route) => route.path)).to.include('/ssologin');
    });

    it('should render the route when authenticated', async () => {
      render(protectedRoutes(), true);

      await waitForView('profile');
    });

    it('should redirect to the given path when not authenticated', async () => {
      render(protectedRoutes('/login'), false);

      await waitForView('login');
    });

    it('should redirect to the login route when not authenticated', async () => {
      render(protectedRoutes(), false);

      await waitFor(
        () => window.location.hash === loginLink,
        () => `Expected a redirect to '${loginLink}', got '${window.location.hash}'`
      );
    });
  });
});
