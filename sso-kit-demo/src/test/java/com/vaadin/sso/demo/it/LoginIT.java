/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */

package com.vaadin.sso.demo.it;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@UsePlaywright
class LoginIT {

    static final String APP_URL = System.getProperty("app.url",
            "http://localhost:8080");

    private static final LocatorAssertions.ContainsTextOptions SLOW_CONTAINS = new LocatorAssertions.ContainsTextOptions()
            .setTimeout(30_000);

    private static final LocatorAssertions.HasValueOptions SLOW_VALUE = new LocatorAssertions.HasValueOptions()
            .setTimeout(30_000);

    private static final LocatorAssertions.IsVisibleOptions SLOW_VISIBLE = new LocatorAssertions.IsVisibleOptions()
            .setTimeout(30_000);

    @Test
    void userLoginShowsUserIdentityAndRoles(Page page) {
        loginAs(page, "user", "user");

        assertThat(page.locator("vaadin-text-field#name input"))
                .hasValue("John Doe", SLOW_VALUE);
        assertThat(page.locator("vaadin-email-field#email input"))
                .hasValue("user@example.com", SLOW_VALUE);

        Locator roles = page.locator("#roles");
        assertThat(roles).containsText("ROLE_user", SLOW_CONTAINS);
        assertThat(roles).containsText("view-profile", SLOW_CONTAINS);
        assertThat(roles).not().containsText("ROLE_admin");
        assertThat(roles).not().containsText("manage-users");
    }

    @Test
    void adminLoginShowsAdminIdentityAndRoles(Page page) {
        loginAs(page, "admin", "admin");

        assertThat(page.locator("vaadin-text-field#name input"))
                .hasValue("Jane Doe", SLOW_VALUE);
        assertThat(page.locator("vaadin-email-field#email input"))
                .hasValue("admin@example.com", SLOW_VALUE);

        Locator roles = page.locator("#roles");
        assertThat(roles).containsText("ROLE_user", SLOW_CONTAINS);
        assertThat(roles).containsText("ROLE_admin", SLOW_CONTAINS);
        assertThat(roles).containsText("view-profile", SLOW_CONTAINS);
        assertThat(roles).containsText("manage-users", SLOW_CONTAINS);
    }

    private void loginAs(Page page, String username, String password) {
        page.navigate(APP_URL + "/profile", new Page.NavigateOptions()
                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

        // Demo's LoginView shows a single "Login with Keycloak" anchor
        // that points at Spring Security's /oauth2/authorization/keycloak.
        page.locator("a[href*='oauth2/authorization/keycloak']").click();

        // Keycloak login form (stable ids across 20-26).
        page.locator("#username").fill(username);
        page.locator("#password").fill(password);
        page.locator("#kc-login").click();

        // Back on the app — wait for the profile page to bootstrap. Spring
        // Security may append a `?continue` query param on the redirect, so
        // wait for the view's name field to materialize rather than for an
        // exact URL match.
        assertThat(page.locator("vaadin-text-field#name input"))
                .isVisible(SLOW_VISIBLE);
    }
}
