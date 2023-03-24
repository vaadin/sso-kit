/*-
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
import { makeAutoObservable } from "mobx";
import { logout } from "@hilla/frontend";
import EndpointImportError from "./EndpointImportError.js";
import type { AccessProps } from "./AccessProps.js";
import type { SingleSignOnData } from "./SingleSignOnData.js";
import type { Subscription } from "@hilla/frontend";
import type { User } from "./User.js";

/**
 * Type definition for the back-channel logout endpoint subscribe method return type.
 */
type Message = {
  message?: string;
};

/**
 * A store for authentication information.
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
   * If true, the user has been logged out from the authentication provider.
   */
  backChannelLogoutHappened = false;
  /**
   * A list of the authentication providers.
   */
  registeredProviders: string[] = [];
  /**
   * The subscription to the back-channel logout event.
   */
  #logoutSubscription?: Subscription<Message>;

  constructor(singleSignOnData: SingleSignOnData) {
    makeAutoObservable(this);

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
        (registeredProviders: string[]) =>
          (this.registeredProviders = registeredProviders),
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
        (user: User) => (this.user = user),
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
            this.#logoutSubscription!.onNext(() => {
              this.backChannelLogoutHappened = true;
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
    location.href = this.loginUrl!;
  };

  /**
   * Logouts from the application and redirects the user to the authentication provider's login page.
   */
  loginAgain = async () => {
    await logout();
    location.href = this.loginUrl!;
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
    location.href = this.logoutUrl!;
  };

  /**
   * Clears the authentication information.
   */
  clearSingleSignOnData = () => {
    this.authenticated = false;
    this.roles = [];
    this.logoutUrl = undefined;
    this.backChannelLogoutHappened = false;
    if (this.#logoutSubscription) {
      this.#logoutSubscription.cancel();
      this.#logoutSubscription = undefined;
    }
  };
}

export default function getSingleSignOnContext() {
  return new SingleSignOnContext(window.Vaadin.SingleSignOnData!);
}
