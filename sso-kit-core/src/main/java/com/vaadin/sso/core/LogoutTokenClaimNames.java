/*-
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.sso.core;

/**
 * The names of the &quot;claims&quot; defined by the OpenID Connect
 * Back-Channel Logout 1.0 specification that can be returned in the Logout
 * Token.
 *
 * @author Vaadin Ltd
 * @since 1.0
 * @see https://openid.net/specs/openid-connect-backchannel-1_0.html#LogoutToken
 */
public interface LogoutTokenClaimNames {

    /**
     * The {@code sid} claim (session identifier).
     */
    String SID = "sid";

    /**
     * The {@code events} claim.
     */
    String EVENTS = "events";
}
