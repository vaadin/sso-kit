import { Route } from '@vaadin/router';
import { appStore } from './stores/app-store';
import './views/about/about-view';
import './views/helloworld/hello-world-view';
import './views/main-layout';

export type ViewRoute = Route & {
  title?: string;
  icon?: string;
  requiresLogin?: boolean;
  rolesAllowed?: string[];
  children?: ViewRoute[];
};

export const hasAccess = (route: Route) => {
  const viewRoute = route as ViewRoute;
  if (viewRoute.requiresLogin && !appStore.loggedIn) {
    return false;
  }

  if (viewRoute.rolesAllowed) {
    return viewRoute.rolesAllowed.some((role) => appStore.isUserInRole(role));
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
      location.href = `/oauth2/authorization/${appStore.registeredProviders[0]}`;
    },
  },
  {
    path: '',
    component: 'main-layout',
    children: views,
  },
];
