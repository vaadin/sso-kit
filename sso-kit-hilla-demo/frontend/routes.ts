import { Route } from '@vaadin/router';
import { AccessControl, ssoKit } from './kit/sso-kit';
import './views/about/about-view';
import './views/helloworld/hello-world-view';
import './views/main-layout';

export type ViewRoute = Route & AccessControl & {
  title?: string;
  icon?: string;
  children?: ViewRoute[];
};

export const hasAccess = (route: Route) => {
  const viewRoute = route as ViewRoute;
  if (viewRoute.requiresLogin && !ssoKit.loggedIn) {
    return false;
  }

  if (viewRoute.rolesAllowed) {
    return viewRoute.rolesAllowed.some((role) => ssoKit.isUserInRole(role));
  }
  return true;
};

export const views: ViewRoute[] = [
  // place routes below (more info https://hilla.dev/docs/routing)
  {
    path: '',
    component: 'about-view',
    icon: '',
    title: '',
  },
  {
    path: 'hello',
    requiresLogin: true,
    icon: 'la la-globe',
    title: 'Hello World',
    action: async (_context, _command) => {
      return hasAccess(_context.route) ? _command.component('hello-world-view') : _command.redirect('login');
    },
  },
  {
    path: 'about',
    component: 'about-view',
    icon: 'la la-file',
    title: 'About',
  },
];
export const routes: ViewRoute[] = [
  {
    path: 'login',
    icon: '',
    title: 'Login',
    action: async (_context, _command) => {
      location.href = ssoKit.mainLoginUrl;
    },
  },
  {
    path: '',
    component: 'main-layout',
    children: views,
  },
];
