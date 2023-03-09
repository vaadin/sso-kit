import {logout, Subscription} from "@hilla/frontend";
import {makeAutoObservable} from "mobx";

/**
 * Used to declare the Hilla.SingleSignOnData variable type.
 */
declare global {
    const Hilla: {
        SingleSignOnData: string;
    };
}

/**
 * Type definition for the authentication information.
 */
type SingleSignOnData = {
    authenticated: boolean;
    roles: string[];
    loginLink?: string;
    logoutLink?: string;
    backChannelLogoutEnabled: boolean;
}

/**
 * Type definition for the authenticated user information.
 */
export type User = {
    birthdate?: string;
    email?: string;
    familyName?: string;
    fullName?: string;
    gender?: string;
    givenName?: string;
    locale?: string;
    middleName?: string;
    nickName?: string;
    phoneNumber?: string;
    picture?: string;
    preferredUsername?: string;
}

/**
 * Type definition which can be added to the view routes to indicate that
 * authentication is needed to access that route.
 */
export type AccessProps = {
    requiresLogin?: boolean;
}

/**
 * A store for authentication information.
 */
class SingleSignOnContext {

    /**
     * Contains the information about the authenticated user.
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
    loginUrl?: string = undefined;

    /**
     * The URL which will be called to log out from the authentication provider.
     */
    logoutUrl?: string = undefined;

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
    private logoutSubscription?: Subscription<any>;

    /**
     * Constructor for the SingleSignOnContext.
     */
    constructor() {
        makeAutoObservable(this);

        const singleSignOnData = JSON.parse(Hilla.SingleSignOnData) as SingleSignOnData;
        this.authenticated = singleSignOnData.authenticated;
        this.roles = singleSignOnData.roles;
        this.loginUrl = singleSignOnData.loginLink;
        this.logoutUrl = singleSignOnData.logoutLink;
        this.backChannelLogoutEnabled = singleSignOnData.backChannelLogoutEnabled;

        // @ts-ignore: the imported file might not exist, in that case the user will be empty
        import("Frontend/generated/UserEndpoint").then((endpoint) => {
            endpoint.getAuthenticatedUser().then(
                (user: User) => {
                    this.user = user;
                },
                (reason: any) => {
                    console.error(reason);
                }
            );
        });

        // @ts-ignore: the imported file might not exist, in that case the registeredProviders will be empty
        import("Frontend/generated/SingleSignOnEndpoint").then(
            (endpoint) => {
                this.registeredProviders = endpoint.getRegisteredProviders();
            },
            (reason: any) => {
                console.error(reason);
            }
        );

        if (this.authenticated && this.backChannelLogoutEnabled) {
            // @ts-ignore: the imported file might not exist, in that case the backChannelLogoutEnabled will be false
            import("Frontend/generated/BackChannelLogoutEndpoint").then(
                (endpoint) => {
                    this.logoutSubscription = endpoint.subscribe();
                    this.logoutSubscription!.onNext(() => {
                        this.backChannelLogoutHappened = true;
                        this.logoutSubscription!.cancel();
                    });
                },
                (reason: any) => {
                    console.error(reason);
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
    }

    /**
     * Logouts from the application and the authentication provider.
     */
    logout = async () => {
        await logout();
        this.logoutUrl && (location.href = this.logoutUrl);
    }

    /**
     * Redirects to the authentication provider's login page.
     */
    login = () => {
        this.loginUrl && (location.href = this.loginUrl);
    }

    /**
     * Logouts from the application and clears the user's authentication information.
     */
    stayOnPage = async () => {
        await logout();
        this.clearSingleSignOnData();
    }

    /**
     * Logouts from the application and redirects the user to the authentication provider's login page.
     */
    loginAgain = async () => {
        await logout();
        this.loginUrl && (location.href = this.loginUrl);
    }

    /**
     * Clears the authentication information.
     */
    clearSingleSignOnData = () => {
        this.authenticated = false;
        this.roles = [];
        this.logoutUrl = undefined;
        this.backChannelLogoutHappened = false;
        if (this.logoutSubscription) {
            this.logoutSubscription = undefined;
        }
    }
}

export const singleSignOnContext = new SingleSignOnContext();
