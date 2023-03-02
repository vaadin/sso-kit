import type { Subscription } from "@hilla/frontend";
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

export type AccessProps = {
    requiresLogin?: boolean;
};

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
            // @ts-ignore: the imported file might not exist, but in that case backChannelLogoutEnabled will be false
            import("Frontend/generated/BackChannelLogoutEndpoint").then((endpoint) => {
                this.logoutSubscription = endpoint.subscribe();

                this.logoutSubscription!.onNext(async () => {
                    this.backChannelLogoutHappened = true;
                });
            });
        }
    }
}

export const ssoKit = new SsoKit();
