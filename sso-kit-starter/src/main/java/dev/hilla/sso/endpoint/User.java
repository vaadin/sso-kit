/*-
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package dev.hilla.sso.endpoint;

import java.util.List;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import dev.hilla.Nonnull;
import dev.hilla.sso.starter.SingleSignOnContext;

/**
 * A convenience class that contains the information about the current user.
 * Most fields are directly mapped to the OidcUser class.
 */
public class User {

    private String birthdate;
    private String email;
    private String familyName;
    private String fullName;
    private String gender;
    private String givenName;
    private String locale;
    private String middleName;
    private String nickName;
    private String phoneNumber;
    private String picture;
    private String preferredUsername;

    @Nonnull
    private List<@Nonnull String> roles = List.of();

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPreferredUsername() {
        return preferredUsername;
    }

    public void setPreferredUsername(String preferredUsername) {
        this.preferredUsername = preferredUsername;
    }

    @Nonnull
    public List<@Nonnull String> getRoles() {
        return roles;
    }

    public void setRoles(@Nonnull List<@Nonnull String> roles) {
        this.roles = roles;
    }

    /**
     * Maps the OidcUser to a User object.
     *
     * @param oidcUser
     *            the OidcUser
     * @return the User object, containing the information from the OidcUser and
     *         a mapping of the roles.
     */
    public static User from(OidcUser oidcUser) {
        User user = new User();
        user.setBirthdate(oidcUser.getBirthdate());
        user.setEmail(oidcUser.getEmail());
        user.setFamilyName(oidcUser.getFamilyName());
        user.setFullName(oidcUser.getFullName());
        user.setGender(oidcUser.getGender());
        user.setGivenName(oidcUser.getGivenName());
        user.setLocale(oidcUser.getLocale());
        user.setMiddleName(oidcUser.getMiddleName());
        user.setNickName(oidcUser.getNickName());
        user.setPhoneNumber(oidcUser.getPhoneNumber());
        user.setPicture(oidcUser.getPicture());
        user.setPreferredUsername(oidcUser.getPreferredUsername());

        user.setRoles(SingleSignOnContext.userRoles(oidcUser));
        return user;
    }
}
