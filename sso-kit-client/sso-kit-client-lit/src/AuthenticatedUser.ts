/*-
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
import {makeAutoObservable} from "mobx";

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
 * A store for authenticated user..
 */
class AuthenticatedUser {

    /**
     * The user's birthdate.
     */
    birthdate?: string = undefined;
    /**
     * The user's email.
     */
    email?: string = undefined;
    /**
     * The user's family name.
     */
    familyName?: string = undefined;
    /**
     * The user's full name.
     */
    fullName?: string = undefined;
    /**
     * The user's gender.
     */
    gender?: string = undefined;
    /**
     * The user's given name.
     */
    givenName?: string = undefined;
    /**
     * The user's locale.
     */
    locale?: string = undefined;
    /**
     * The user's middle name.
     */
    middleName?: string = undefined;
    /**
     * The user's nickname.
     */
    nickName?: string = undefined;
    /**
     * The user's phone number.
     */
    phoneNumber?: string = undefined;
    /**
     * The user's picture.
     */
    picture?: string = undefined;
    /**
     * The user's preferred username.
     */
    preferredUsername?: string = undefined;

    /**
     * Constructor for the AuthenticatedUser.
     */
    constructor() {
        makeAutoObservable(this);

        // @ts-ignore: the imported file might not exist,
        // in that case the authenticated user data will be empty
        import("Frontend/generated/UserEndpoint").then((endpoint) => {
                endpoint.getAuthenticatedUser().then(
                    (user: User) => {
                        this.birthdate = user?.birthdate;
                        this.email = user?.email;
                        this.familyName = user?.familyName;
                        this.fullName = user?.fullName;
                        this.gender = user?.gender;
                        this.givenName = user?.givenName;
                        this.locale = user?.locale;
                        this.middleName = user?.middleName;
                        this.nickName = user?.nickName;
                        this.phoneNumber = user?.phoneNumber;
                        this.picture = user?.picture;
                        this.preferredUsername = user?.preferredUsername;
                    },
                    (reason: any) => {
                        console.error(reason);
                    }
                );
            },
            (reason: any) => {
                console.error(reason);
            }
        );
    }
}

export const authenticatedUser = new AuthenticatedUser();
