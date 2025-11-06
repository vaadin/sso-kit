/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */
import type { ReactNode } from 'react';
import React from 'react';
import type { RouteObject } from 'react-router-dom';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useSsoContext } from './useSsoContext.js';
import type { AccessProps } from '../../core/src/AccessProps.js';

interface ProtectedRouteProps {
  redirectPath?: string;
  route: ReactNode;
}

function ProtectedRoute({ redirectPath, route }: ProtectedRouteProps): JSX.Element | null {
  const { authenticated, ssoContextInitialized } = useSsoContext();
  const location = useLocation();

  if (!ssoContextInitialized) {
    return <div></div>;
  }

  if (!authenticated) {
    if (redirectPath === undefined) {
      redirectPath = '/ssologin';
    }
    return <Navigate to={redirectPath} state={{ from: location }} replace />;
  }

  return route ? (route as JSX.Element) : <Outlet />;
}

const collectRoutes = <T,>(routes: T[]): T[] => {
  const allRoutes: T[] = [];
  routes.forEach((route) => {
    allRoutes.push(route);
    if ((route as RouteObject).children !== undefined) {
      allRoutes.push(...collectRoutes((route as RouteObject).children as T[]));
    }
  });
  return allRoutes;
};

const protectRoute = <T,>(route: T, redirectPath?: string): void => {
  if ((route as AccessProps).requireAuthentication) {
    (route as RouteObject).element = (
      <ProtectedRoute redirectPath={redirectPath} route={(route as RouteObject).element} />
    );
  }
};

/**
 * Adds protection to routes that require authentication.
 * These routes should contain the {@link AccessProps.requireAuthentication}
 * property with value true.
 *
 * @param routes the routes to check if any of them needs to be protected
 * @param redirectPath (Optional) the path to redirect to if the route is
 * protected and the user is not authenticated. The default value is the
 * '/ssologin' path which redirects the user to the providers login page
 * @returns the routes extended with protection if needed
 */
export const protectRoutes = <T,>(routes: T[], redirectPath?: string): T[] => {
  const allRoutes: T[] = collectRoutes(routes);
  allRoutes.forEach((route) => {
    if ((route as AccessProps).requireAuthentication) {
      protectRoute(route, redirectPath);
    }
  });

  (routes as RouteObject[]).push({
    path: '/ssologin',
    Component: () => {
      const { loginUrl } = useSsoContext();
      window.location.href = loginUrl;
      return null;
    }
  });

  return routes;
};
