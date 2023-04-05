/*-
 * Copyright (C) 2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
import { logout } from "@hilla/frontend";
import type { Subscription } from "@hilla/frontend";
import EndpointImportError from "./EndpointImportError.js";
import type { AccessProps } from "./AccessProps.js";
import type { SingleSignOnData } from "./SingleSignOnData.js";
import type { User } from "./User.js";

/**
 * Definition of the back-channel logout endpoint subscription message.
 */
type Message = {
  message?: string;
};

/**
 * Definition of the back-channel logout callbacks.
 */
type LogoutCallback = () => void;

/**
 * The context of the single sign-on authentication process. It provides
 * authentication information and functions to operate on the current
 * state, such as logging in and out.
 */
export class SingleSignOnContext {

  /**
   * The authenticated user.
   */
  user?: User;

  /**
   * If true, the user has been authenticated.
   */
  authenticated = false;

  /**
   * The user roles.
   */
  roles: string[] = [];

  /**
   * The URL which will be called to log in to the authentication provider.
   */
  loginUrl?: string;

  /**
   * The URL which will be called to log out from the authentication provider.
   */
  logoutUrl?: string;

  /**
   * If true, the application will listen to the back-channel logout events.
   */
  backChannelLogoutEnabled = false;

  /**
   * A list of the authentication providers.
   */
  registeredProviders: string[] = [];

  /**
   * The subscription to the back-channel logout event.
   */
  #logoutSubscription?: Subscription<Message>;

  /**
   * The back-channel logout subscription callback.
   */
  #logoutSubscriptionCallback?: LogoutCallback;

  constructor(singleSignOnData: SingleSignOnData) {
    this.authenticated = singleSignOnData.authenticated;
    this.roles = singleSignOnData.roles;
    this.loginUrl = singleSignOnData.loginLink;
    this.logoutUrl = singleSignOnData.logoutLink;
    this.backChannelLogoutEnabled = singleSignOnData.backChannelLogoutEnabled;

    // @ts-ignore: the imported file might not exist,
    // in that case the registeredProviders will be empty
    import("Frontend/generated/SingleSignOnEndpoint.ts")
      .then(
        (endpoint) => endpoint.getRegisteredProviders(),
        (reason) => {
          throw new EndpointImportError("SingleSignOnEndpoint", reason);
        }
      )
      .then(
        (registeredProviders: string[]) => {
          this.registeredProviders = registeredProviders;
        },
        (reason: string) => {
          throw new Error(`Couldn't get registered providers: ${reason}`);
        }
      );

    // @ts-ignore: the imported file might not exist,
    // in that case the authenticated user will be undefined
    import("Frontend/generated/UserEndpoint.ts")
      .then(
        (endpoint) => endpoint.getAuthenticatedUser(),
        (reason) => {
          throw new EndpointImportError("UserEndpoint", reason);
        }
      )
      .then(
        (user: User) => {
          this.user = user;
        },
        (reason: string) => {
          throw new Error(`Couldn't get authenticated user: ${reason}`);
        }
      );

    if (this.authenticated && this.backChannelLogoutEnabled) {
      // @ts-ignore: the imported file might not exist,
      // in that case the backChannelLogoutEnabled will be false
      import("Frontend/generated/BackChannelLogoutEndpoint.ts")
        .then(
          (endpoint) => endpoint.subscribe(),
          (reason) => {
            throw new EndpointImportError("BackChannelLogoutEndpoint", reason);
          }
        )
        .then(
          (subscription: Subscription<Message>) => {
            this.#logoutSubscription = subscription;
            this.#logoutSubscription.onNext(() => {
              this.#logoutSubscriptionCallback && this.#logoutSubscriptionCallback();
              this.#logoutSubscription!.cancel();
            });
          },
          (reason: string) => {
            throw new Error(
              `Couldn't subscribe to the back-channel logout events: ${reason}`
            );
          }
        );
    }
  }

  /**
   * Checks if the user has access to the given route.
   *
   * @param route the route to check
   * @returns true if the user has access to the given route, false otherwise
   */
  hasUserAccess = (route: AccessProps) => {
    return !route.requiresLogin || this.authenticated;
  };

  /**
   * Checks if the user has the given role.
   * @param role the role to check
   * @returns true if the user has the given role, false otherwise
   */
  isUserInRole = (role: string) => {
    return this.roles.includes(role);
  };

  /**
   * Redirects to the authentication provider's login page.
   */
  login = () => {
    window.location.href = this.loginUrl!;
  };

  /**
   * Logs out from the application and clears the user's authentication information.
   */
  stayOnPage = async () => {
    await logout();
    this.clearSingleSignOnData();
  };

  /**
   * Logs out from the application and the authentication provider.
   *
   * @param redirectUrl the location to redirect the user to after logout (defaults to {@link #logoutUrl})
   */
  logout = async (redirectUrl: string = this.logoutUrl!) => {
    await logout();
    window.location.href = redirectUrl!;
  };

  /**
   * Adds a callback to the back-channel logout subscription callbacks.
   *
   * @param callback a function executed when back-channel logout happens
   */
  onBackChannelLogout(callback: LogoutCallback) {
    this.#logoutSubscriptionCallback = callback;
  }

  /**
   * Clears the authentication information.
   */
  clearSingleSignOnData = () => {
    this.authenticated = false;
    this.roles = [];
    this.logoutUrl = undefined;
    this.#logoutSubscriptionCallback = undefined;
    if (this.#logoutSubscription) {
      this.#logoutSubscription.cancel();
      this.#logoutSubscription = undefined;
    }
  };
}

export default function singleSignOnContext() {
  return new SingleSignOnContext(window.Vaadin.SingleSignOnData!);
}
