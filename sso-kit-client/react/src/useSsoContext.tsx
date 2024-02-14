/*-
 * Copyright (C) 2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
import type { ReactNode } from "react";
import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState,
} from "react";
import type { Subscription } from "@vaadin/hilla-frontend";
import { logout as serverLogout } from "@vaadin/hilla-frontend";
import type { AccessProps } from "../../core/src/AccessProps.js";
import type { SingleSignOnData } from "../../core/src/SingleSignOnData.js";
import type { User } from "../../core/src/User.js";
import { EndpointImportError } from "../../core/src/EndpointImportError.js";

/**
 * Definition of the back-channel logout endpoint subscription message.
 */
type Message = {
  message?: string;
};

/**
 * Definition of the function that is executed when back-channel logout happens.
 */
type LogoutCallback = () => void;

function useSsoContextHook() {
  /**
   * Indicates if the single sing-on context has been initialized.
   */
  const [ssoContextInitialized, setSsoContextInitialized] =
    useState<boolean>(false);

  /**
   * Indicates if the user has been authenticated.
   */
  const [authenticated, setAuthenticated] = useState<boolean>(false);

  /**
   * Contains the user's roles.
   */
  const [roles, setRoles] = useState<string[]>([]);

  /**
   * The URL to call to log in to the authentication provider.
   */
  const [loginUrl, setLoginUrl] = useState<string>("");

  /**
   * The URL to call to log out from the authentication provider.
   */
  const [logoutUrl, setLogoutUrl] = useState<string | undefined>();

  /**
   * A list of the registered authentication provider registration ids.
   */
  const [registrationIds, setRegistrationIds] = useState<
    string[] | undefined
  >();

  /**
   * Contains information about the authenticated user.
   */
  const [user, setUser] = useState<User | undefined>();

  /**
   * Indicates if the back-channel logout is enabled.
   * If true, the application will listen to the back-channel logout events.
   */
  const [backChannelLogoutEnabled, setBackChannelLogoutEnabled] =
    useState<boolean>(false);

  /**
   * The back-channel logout subscription callback.
   */
  const [logoutSubscriptionCallback, setLogoutSubscriptionCallback] = useState<
    LogoutCallback | undefined
  >();

  /**
   * Checks if the user has access to the given route.
   *
   * @param route the route to check
   * @returns true if the user has access to the given route, false otherwise
   */
  const hasAccess = (route: AccessProps) => {
    return !route.requireAuthentication || authenticated;
  };

  /**
   * Checks if the user has the given role.
   *
   * @param role the role to check
   * @returns true if the user has the given role, false otherwise
   */
  const isUserInRole = (role: string) => {
    return roles.includes(role);
  };

  /**
   * Redirects to the authentication provider's login page.
   */
  const login = () => {
    window.location.href = loginUrl;
  };

  /**
   * Logs out from the application and the authentication provider.
   */
  const logout = async () => {
    await serverLogout();
    window.location.href = logoutUrl!;
  };

  /**
   * Adds a function that is executed when the back-channel logout happens.
   *
   * @param callback a function executed when back-channel logout happens
   */
  const onBackChannelLogout = (callback: LogoutCallback) => {
    setLogoutSubscriptionCallback(() => callback);
  };

  /**
   * Updates the single sign-on data in the context.
   */
  const fetchSingleSignOnData = () => {
    // @ts-ignore
    import("Frontend/generated/SingleSignOnEndpoint.ts")
      .then(
        (endpoint) => endpoint.fetchAll(),
        (reason) => {
          throw new EndpointImportError("SingleSignOnEndpoint", reason);
        }
      )
      .then(
        (singleSignOnData: SingleSignOnData) => {
          setAuthenticated(singleSignOnData.authenticated);
          setRoles(singleSignOnData.roles);
          setLoginUrl(singleSignOnData.loginLink);
          setLogoutUrl(singleSignOnData.logoutLink);
          setBackChannelLogoutEnabled(
            singleSignOnData.backChannelLogoutEnabled
          );
        },
        (reason: string) => {
          throw new Error(`Couldn't fetch single sign-on data: ${reason}`);
        }
      );
  };

  const initRegistrationIds = useCallback(async () => {
    const registeredProviders = await import(
      // @ts-ignore
      "Frontend/generated/SingleSignOnEndpoint.ts"
    ).then(
      (endpoint) => endpoint.getRegisteredProviders(),
      (reason) => {
        throw new EndpointImportError("SingleSignOnEndpoint", reason);
      }
    );
    setRegistrationIds(registeredProviders);
  }, []);

  const initUser = useCallback(async () => {
    const authenticatedUser = await import(
      // @ts-ignore
      "Frontend/generated/UserEndpoint.ts"
    ).then(
      (endpoint) => endpoint.getAuthenticatedUser(),
      (reason) => {
        throw new EndpointImportError("UserEndpoint", reason);
      }
    );
    setUser(authenticatedUser);
  }, []);

  const logoutSubscribe = useCallback(async () => {
    if (authenticated && backChannelLogoutEnabled) {
      // @ts-ignore
      await import("Frontend/generated/BackChannelLogoutEndpoint.ts")
        .then(
          (endpoint) => endpoint.subscribe(),
          (reason) => {
            throw new EndpointImportError("BackChannelLogoutEndpoint", reason);
          }
        )
        .then(
          (subscription: Subscription<Message>) => {
            subscription.onNext(() => {
              if (logoutSubscriptionCallback) {
                logoutSubscriptionCallback();
              }
              subscription.cancel();
            });
          },
          (reason: string) => {
            throw new Error(
              `Couldn't subscribe to the back-channel logout events: ${reason}`
            );
          }
        );
    }
  }, [authenticated, backChannelLogoutEnabled, logoutSubscriptionCallback]);

  const initSsoContext = useCallback(
    (singleSignOnData?: SingleSignOnData) => {
      try {
        if (singleSignOnData !== undefined) {
          setAuthenticated(singleSignOnData.authenticated);
          setRoles(singleSignOnData.roles);
          setLoginUrl(singleSignOnData.loginLink);
          setLogoutUrl(singleSignOnData.logoutLink);
          setBackChannelLogoutEnabled(
            singleSignOnData.backChannelLogoutEnabled
          );
        }
        initRegistrationIds();
        initUser();
        logoutSubscribe();
      } finally {
        setSsoContextInitialized(true);
      }
    },
    [initRegistrationIds, initUser, logoutSubscribe]
  );

  useEffect(() => {
    initSsoContext(window.Vaadin.SingleSignOnData);
  }, [initSsoContext]);

  return {
    ssoContextInitialized,
    authenticated,
    roles,
    loginUrl,
    logoutUrl,
    registrationIds,
    user,
    hasAccess,
    isUserInRole,
    login,
    logout,
    onBackChannelLogout,
    fetchSingleSignOnData,
  };
}

type SsoContextType = ReturnType<typeof useSsoContextHook>;

const initialValue: SsoContextType = {
  ssoContextInitialized: false,
  authenticated: false,
  roles: [],
  loginUrl: "",
  logoutUrl: undefined,
  registrationIds: undefined,
  user: undefined,
  hasAccess: () => false,
  isUserInRole: () => false,
  login: () => {
    /* do nothing */
  },
  logout: async () => {
    /* do nothing */
  },
  onBackChannelLogout: () => {
    /* do nothing */
  },
  fetchSingleSignOnData: async () => {
    /* do nothing */
  },
};

const SsoContext = createContext<SsoContextType>(initialValue);

interface SsoProviderProps {
  children: ReactNode;
}

/**
 * Provider for the single sign-on context that can be used to pass the context
 * to the router provider.
 */
export function SsoProvider({ children }: SsoProviderProps) {
  const ssoContext = useSsoContextHook();
  return (
    <SsoContext.Provider value={ssoContext}>{children}</SsoContext.Provider>
  );
}

/**
 * Hook for the context of the single sign-on authentication process. It provides
 * authentication information and functions to operate on the current
 * state, such as logging in and out.
 */
export function useSsoContext() {
  return useContext(SsoContext);
}
