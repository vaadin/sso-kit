/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */
import { expect } from '@esm-bundle/chai';
import type { Commands, RedirectResult } from '@vaadin/router';
import { SingleSignOnContext } from '../src/index.js';
import type { ProtectedRoute } from '../src/SingleSignOnContext.js';
import { singleSignOnData } from './mocks/SingleSignOnEndpointMock.js';

/**
 * The commands the router passes to a route action. Only `redirect` is used by
 * the route protection, the others are here to satisfy the interface.
 */
const commands = {
  component: (name: string) => document.createElement(name),
  prevent: () => ({ cancel: true }),
  redirect: (path: string) => ({ redirect: { pathname: path } })
} as unknown as Commands;

function redirectedTo(result: unknown): string | undefined {
  return (result as RedirectResult | undefined)?.redirect?.pathname;
}

function createContext(authenticated: boolean): SingleSignOnContext {
  return new SingleSignOnContext({ ...singleSignOnData, authenticated });
}

describe('@vaadin/sso-kit-client-lit', () => {
  describe('SingleSignOnContext', () => {
    it('should be exported', async () => {
      expect(SingleSignOnContext).to.be.ok;
    });

    it('should add the login route to the protected routes', async () => {
      const routes = createContext(false).protectRoutes([{ path: 'home', component: 'home-view' }]);

      expect(routes.map((route) => route.path)).to.include('ssologin');
    });

    it('should redirect to the login route when not authenticated', async () => {
      const route: ProtectedRoute = { path: 'profile', component: 'profile-view', requireAuthentication: true };

      createContext(false).protectRoutes([route]);

      expect(redirectedTo(await route.action!.call(route, {} as never, commands))).to.equal('ssologin');
    });

    it('should redirect to the given path when not authenticated', async () => {
      const route: ProtectedRoute = { path: 'profile', component: 'profile-view', requireAuthentication: true };

      createContext(false).protectRoutes([route], 'login');

      expect(redirectedTo(await route.action!.call(route, {} as never, commands))).to.equal('login');
    });

    it('should protect nested routes', async () => {
      const child: ProtectedRoute = { path: 'details', component: 'details-view', requireAuthentication: true };
      const parent: ProtectedRoute = { path: 'profile', component: 'profile-view', children: [child] };

      createContext(false).protectRoutes([parent]);

      expect(redirectedTo(await child.action!.call(child, {} as never, commands))).to.equal('ssologin');
    });

    it('should call the original action when authenticated', async () => {
      const element = document.createElement('div');
      const route: ProtectedRoute = {
        path: 'profile',
        requireAuthentication: true,
        action: () => element
      };

      createContext(true).protectRoutes([route]);

      expect(await route.action!.call(route, {} as never, commands)).to.equal(element);
    });

    it('should not touch routes that do not require authentication', async () => {
      const route: ProtectedRoute = { path: 'home', component: 'home-view' };

      createContext(false).protectRoutes([route]);

      expect(route.action).to.be.undefined;
    });
  });
});
