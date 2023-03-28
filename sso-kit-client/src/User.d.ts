/*-
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
/**
 * Type definition for the authenticated user information.
 */
export type User = Readonly<{
  /**
   * The user's birthdate.
   */
  birthdate?: string;
  /**
   * The user's email.
   */
  email?: string;
  /**
   * The user's family name.
   */
  familyName?: string;
  /**
   * The user's full name.
   */
  fullName?: string;
  /**
   * The user's gender.
   */
  gender?: string;
  /**
   * The user's given name.
   */
  givenName?: string;
  /**
   * The user's locale.
   */
  locale?: string;
  /**
   * The user's middle name.
   */
  middleName?: string;
  /**
   * The user's nickname.
   */
  nickName?: string;
  /**
   * The user's phone number.
   */
  phoneNumber?: string;
  /**
   * The user's picture.
   */
  picture?: string;
  /**
   * The user's preferred username.
   */
  preferredUsername?: string;
}>;
