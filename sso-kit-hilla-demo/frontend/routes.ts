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
    action: ssoKit.protectedView('hello-world-view'),
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
    action: ssoKit.loginView(),
  },
  {
    path: '',
    component: 'main-layout',
    children: views,
  },
];
