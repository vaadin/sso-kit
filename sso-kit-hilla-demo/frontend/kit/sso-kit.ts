import { logout, Subscription } from "@hilla/frontend";
import { Commands, ComponentResult, Context, PreventResult, RedirectResult, Route } from "@vaadin/router";
import { Buffer } from "buffer";
import { makeAutoObservable } from "mobx";

declare global {
  const Hilla: {
    BootstrapSSO?: string;
  };
}

type BootstrapData = {
  authenticated: boolean;
  roles: string[];
  logoutUrl: string | undefined;
  registeredProviders: string[];
  backChannelLogoutEnabled: boolean;
}

type AccessControl = {
  allowed: boolean;
}

export function loginUrl(provider: string) {
  return `/oauth2/authorization/${provider}`;
}

/**
 * A store for authentication information
 */
class SsoKit {
  authenticated = false;

  roles: string[] = [];

  /**
   * A list of authentication providers. The first provider is the main provider.
   */
  registeredProviders: string[] = [];

  /**
   * The URL which will be called to log out from the SSO provider
   */
  logoutUrl: string | undefined = undefined;

  /**
   * If true, the app will listen to back-channel logout events
   */
  backChannelLogoutEnabled = false;

  /**
   * If true, the user has been logged out from the SSO provider
   */
  backChannelLogoutHappened = false;

  /**
   * The subscription to the back-channel logout event
   */
  private logoutSubscription: Subscription<any> | undefined;

  constructor() {
    makeAutoObservable(this);

    const authInfo = JSON.parse(Buffer.from(Hilla.BootstrapSSO!, 'base64').toString('utf-8')) as BootstrapData;
    this.authenticated = authInfo.authenticated;
    this.roles = authInfo.roles;
    this.logoutUrl = authInfo.logoutUrl;
    this.registeredProviders = authInfo.registeredProviders;
    this.backChannelLogoutEnabled = authInfo.backChannelLogoutEnabled;

    if (this.authenticated && this.backChannelLogoutEnabled) {
      import("Frontend/generated/BackChannelLogoutEndpoint").then((endpoint) => {
        this.logoutSubscription = endpoint.subscribe();

        this.logoutSubscription.onNext(async () => {
          this.backChannelLogoutHappened = true;
        });
      });
    }
  }

  /**
   * Clears the authentication information
   */
  clearUserInfo = () => {
    this.authenticated = false;
    this.logoutUrl = undefined;
    this.backChannelLogoutHappened = false;

    if (this.logoutSubscription) {
      this.logoutSubscription.cancel();
      this.logoutSubscription = undefined;
    }
  }

  /**
   * Returns true if the user is logged in
   */
  get loggedIn() {
    return this.authenticated;
  }

  /**
   * Checks if the user has the given role
   * @param role the role to check
   * @returns true if the user has the given role
   */
  isUserInRole = (role: string) => {
    return this.roles.includes(role);
  }

  /**
   * Logout from the application and the provider. This function must be called
   * when the user clicks the logout button, for example.
   */
  logoutFromApp = async () => {
    await logout(); // Logout on the server
    this.logoutUrl && (location.href = this.logoutUrl); // Logout on the provider
  }

  /**
   * Redirects the user to the SSO provider to login. This function must be called
   * when the user clicks the login button, for example.
   */
  relogin = () => {
    location.href = `/oauth2/authorization/${this.registeredProviders[0]}`;
  }

  /**
   * Logout on the application server and on the client. This function must be called
   * when the user has been logged out from the SSO provider.
   */
  logoutFromProvider = async () => {
    await logout(); // Logout on the server
    this.clearUserInfo(); // Logout on the client
  }

  get loginUrls() {
    // create a map of providers as keys and login URLs as values
    return this.registeredProviders.map((provider) => ({ name: provider, link: loginUrl(provider) }));
  }

  get mainLoginUrl() {
    return this.loginUrls[0].link;
  }

  private static _ctx: Context = {
    pathname: '',
    params: {},
    route: {
      component: '',
      path: '',
      redirect: '',
    },
    search: '',
    hash: '',
    next: () => new Promise(() => null),
  };

  private static _cmd: Commands = {
    component: (_name: string) => ({ allowed: true } as unknown as ComponentResult),
    redirect: (name: string) => ({ allowed: name !== 'login' } as unknown as RedirectResult),
    prevent: () => ({} as PreventResult),
  };

  isForbidden = (route: Route) => {
    try {
      return route.action && !(route.action(SsoKit._ctx, SsoKit._cmd) as unknown as AccessControl).allowed;
    } catch (e: any) {
      // error can happen when an action uses our mock context and command and expects some valid values
      return false;
    }
  }

  protectedView = (componentName: string, rolesAllowed?: string[]) => {
    return (context: Context, command: Commands) => {
      let access = this.loggedIn;

      if (rolesAllowed) {
        access &&= rolesAllowed.some((role) => this.isUserInRole(role));
      }

      return access ? command.component(componentName) : command.redirect('login');
    }
  };

  loginView = () => {
    return (_context: Context, _command: Commands) => {
      location.href = ssoKit.mainLoginUrl;
    }
  };
}

export const ssoKit = new SsoKit();
