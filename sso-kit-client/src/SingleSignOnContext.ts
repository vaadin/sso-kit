/*-
 * Copyright (C) 2022 Vaadin Ltd
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
 * Type definition for the back-channel logout endpoint subscribe message.
 */
type Message = {
  message?: string;
};

/**
 * Type definition for the back-channel logout callbacks.
 */
type LogoutCallback = () => void;

/**
 * A store for authentication information.
 */
export class SingleSignOnContext {
  /**
   * Indicates if the user has been authenticated.
   */
  authenticated = false;

  /**
   * Contains the user's roles.
   */
  roles: string[] = [];

  /**
   * The URL to call to log in to the authentication provider.
   */
  loginUrl?: string;

  /**
   * The URL to call to log out from the authentication provider.
   */
  logoutUrl?: string;

  /**
   * A list of the registered authentication providers' registration ids.
   */
  #registrationIds?: Promise<string[]>;

  /**
   * Contains information about the authenticated user.
   */
  #user?: Promise<User>;

  /**
   * The subscription to the back-channel logout event.
   */
  #logoutSubscription?: Subscription<Message>;

  /**
   * The back-channel logout subscription callbacks.
   */
  #logoutSubscriptionCallbacks: LogoutCallback[] = [];

  constructor(singleSignOnData: SingleSignOnData) {
    this.authenticated = singleSignOnData.authenticated;
    this.roles = singleSignOnData.roles;
    this.loginUrl = singleSignOnData.loginLink;
    this.logoutUrl = singleSignOnData.logoutLink;

    this.#registrationIds = import(
      // @ts-ignore: the imported file might not exist,
      // in that case the registeredProviders will be empty
      "Frontend/generated/SingleSignOnEndpoint.ts"
    ).then(
      (endpoint) => endpoint.getRegisteredProviders(),
      (reason) => {
        throw new EndpointImportError("SingleSignOnEndpoint", reason);
      }
    );

    // @ts-ignore: the imported file might not exist,
    // in that case the authenticated user will be undefined
    this.#user = import("Frontend/generated/UserEndpoint.ts").then(
      (endpoint) => endpoint.getAuthenticatedUser(),
      (reason) => {
        throw new EndpointImportError("UserEndpoint", reason);
      }
    );

    if (this.authenticated && singleSignOnData.backChannelLogoutEnabled) {
      // @ts-ignore: the imported file might not exist,
      // in that case no subscription will happen to the back-channel logout event
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
            this.#logoutSubscription!.onNext(() => {
              this.#logoutSubscriptionCallbacks.forEach((callback) =>
                callback()
              );
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
   * Gets a list of the registered authentication providers' registration ids.
   *
   * @returns a Promise with a list of the registration ids
   */
  getRegistrationIds = (): Promise<string[]> | undefined => {
    return this.#registrationIds;
  };

  /**
   * Gets the authenticated user.
   *
   * @returns a Promise with the user object
   */
  getUser = (): Promise<User> | undefined => {
    return this.#user;
  };

  /**
   * Checks if the user has access to the given route.
   *
   * @param route the route to check
   * @returns true if the user has access to the given route, false otherwise
   */
  hasAccess = (route: AccessProps) => {
    return !route.requiresLogin || this.authenticated;
  };

  /**
   * Checks if the user has the given role.
   *
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
   * Logouts from the application and redirects the user to the authentication provider's login page.
   */
  loginAgain = async () => {
    await logout();
    window.location.href = this.loginUrl!;
  };

  /**
   * Logouts from the application and clears the user's authentication information.
   */
  stayOnPage = async () => {
    await logout();
    this.clearSingleSignOnData();
  };

  /**
   * Logouts from the application and the authentication provider.
   */
  logout = async () => {
    await logout();
    window.location.href = this.logoutUrl!;
  };

  /**
   * Adds a callback to the back-channel logout subscription callbacks.
   */
  onBackChannelLogout(callback: LogoutCallback) {
    this.#logoutSubscriptionCallbacks.push(callback);
  }

  /**
   * Clears the authentication information.
   */
  clearSingleSignOnData = () => {
    this.authenticated = false;
    this.roles = [];
    this.logoutUrl = undefined;
    this.#registrationIds = undefined;
    this.#user = undefined;
    this.#logoutSubscriptionCallbacks = [];
    if (this.#logoutSubscription) {
      this.#logoutSubscription.cancel();
      this.#logoutSubscription = undefined;
    }
  };
}

export default function singleSignOnContext() {
  return new SingleSignOnContext(window.Vaadin.SingleSignOnData!);
}
