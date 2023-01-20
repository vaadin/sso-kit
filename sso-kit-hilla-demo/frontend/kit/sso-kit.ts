import { logout, Subscription } from "@hilla/frontend";
import { Commands, Context } from "@vaadin/router";
import Message from "Frontend/generated/dev/hilla/sso/endpoint/BackChannelLogoutSubscription/Message";
import User from "Frontend/generated/dev/hilla/sso/endpoint/User";
import { SingleSignOnEndpoint } from "Frontend/generated/endpoints";
import { makeAutoObservable } from "mobx";

export type AccessControl = {
    requiresLogin?: boolean;
    rolesAllowed?: string[];
};

export function loginUrl(provider: string) {
    return `/oauth2/authorization/${provider}`;
}

/**
 * A store for authentication information
 */
class SsoKit {
    /**
     * The currently logged in user. If undefined, the user is not logged in.
     */
    user: User | undefined = undefined;

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
    private logoutSubscription: Subscription<Message> | undefined;

    constructor() {
        makeAutoObservable(this);
    }

    /**
     * Fetches the authentication information from the server
     */
    fetchAuthInfo = async () => {
        const authInfo = await SingleSignOnEndpoint.allData();
        this.user = authInfo.user;
        this.logoutUrl = authInfo.logoutUrl;
        this.registeredProviders = authInfo.registeredProviders;
        this.backChannelLogoutEnabled = authInfo.backChannelLogoutEnabled;

        if (this.user && this.backChannelLogoutEnabled) {
            this.logoutSubscription = SingleSignOnEndpoint.backChannelLogoutSubscription();

            this.logoutSubscription.onNext(async () => {
                this.backChannelLogoutHappened = true;
            });
        }
    }

    /**
     * Clears the authentication information
     */
    clearUserInfo = () => {
        this.user = undefined;
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
        return !!this.user;
    }

    /**
     * Checks if the user has the given role
     * @param role the role to check
     * @returns true if the user has the given role
     */
    isUserInRole = (role: string) => {
        return this.user?.roles?.includes(role);
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

    hasAccess = (route: AccessControl) => {
        if (route.requiresLogin && !this.loggedIn) {
            return false;
        }

        if (route.rolesAllowed) {
            return route.rolesAllowed.some((role) => this.isUserInRole(role));
        }

        return true;
    };

    protectedView = (componentName: string) => {
        return (context: Context, command: Commands) => {
            return this.hasAccess(context.route as AccessControl) ? command.component(componentName) : command.redirect('login');
        }
    };

    loginView = () => {
        return (_context: Context, _command: Commands) => {
            location.href = ssoKit.mainLoginUrl;
        }
    };
}

export const ssoKit = new SsoKit();
