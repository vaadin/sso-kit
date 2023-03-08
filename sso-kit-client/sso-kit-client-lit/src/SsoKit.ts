import { logout, Subscription } from "@hilla/frontend";
import { Buffer } from "buffer";
import { makeAutoObservable } from "mobx";

declare global {
    const Hilla: {
        SSO?: string;
    };
}

type SingleSignOnData = {
    authenticated: boolean;
    roles: string[];
    loginLink: string | undefined;
    logoutLink: string | undefined;
    backChannelLogoutEnabled: boolean;
}

type AccessProps = {
    requiresLogin?: boolean;
}

export function loginUrl(provider: string) {
    return `/oauth2/authorization/${provider}`;
}

/**
 * A store for authentication information
 */
class SsoKit {
    /**
     * If true, the user has been authenticated
     */
    authenticated = false;

    /**
     * The user roles
     */
    roles: string[] = [];

    /**
     * The URL which will be called to log in to the SSO provider
     */
    loginUrl: string | undefined = undefined;

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
     * A list of authentication providers. The first provider is the main provider.
     */
    registeredProviders: string[] = [];

    /**
     * The subscription to the back-channel logout event
     */
    private logoutSubscription: Subscription<any> | undefined;

    constructor() {
        makeAutoObservable(this);

        const authInfo = JSON.parse(Buffer.from(Hilla.SSO!, 'base64').toString('utf-8')) as SingleSignOnData;
        this.authenticated = authInfo.authenticated;
        this.roles = authInfo.roles;
        this.loginUrl = authInfo.loginLink;
        this.logoutUrl = authInfo.logoutLink;
        this.backChannelLogoutEnabled = authInfo.backChannelLogoutEnabled;

        // @ts-ignore: the imported file might not exist, but in that case registeredProviders will be empty
        import("Frontend/generated/SingleSignOnEndpoint").then((endpoint) => {
            this.registeredProviders = endpoint.getRegisteredProviders();
        });

        if (this.authenticated && this.backChannelLogoutEnabled) {
            // @ts-ignore: the imported file might not exist, but in that case backChannelLogoutEnabled will be false
            import("Frontend/generated/BackChannelLogoutEndpoint").then((endpoint) => {
                this.logoutSubscription = endpoint.subscribe();
                this.logoutSubscription!.onNext(() => {
                    this.backChannelLogoutHappened = true;
                    this.logoutSubscription!.cancel();
                });
            });
        }
    }

    /**
     * Clears the authentication information
     */
    clearAuthInfo = () => {
        this.authenticated = false;
        this.roles = [];
        this.logoutUrl = undefined;
        this.backChannelLogoutHappened = false;
        if (this.logoutSubscription) {
            this.logoutSubscription = undefined;
        }
    }

    /**
     * Returns the authentication providers' login URLs.
     */
    get loginUrls() {
        // create a map of providers as keys and login URLs as values
        return this.registeredProviders.map((provider) => ({ name: provider, link: loginUrl(provider) }));
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
    signOut = async () => {
        await logout(); // Logout on the server
        this.logoutUrl && (location.href = this.logoutUrl); // Logout on the provider
    }

    /**
     * Login to the provider.
     */
    signIn = () => {
        this.loginUrl && (location.href = this.loginUrl); // Redirect to the provider login page
    }

    /**
     * Logout from the application and clear the users authentication information.
     */
    stayOnPage = async () => {
        await logout(); // Logout on the server
        this.clearAuthInfo(); // Logout on the client
    }

    /**
     * Logout from the application and redirect the user to the provider login page
     */
    loginAgain = async () => {
        await logout(); // Logout on the server
        this.loginUrl && (location.href = this.loginUrl); // Redirect to the provider login page
    }
}

export const ssoKit = new SsoKit();
