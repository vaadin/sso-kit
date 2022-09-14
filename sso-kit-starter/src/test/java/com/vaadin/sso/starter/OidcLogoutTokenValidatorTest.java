package com.vaadin.sso.starter;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;

import com.vaadin.sso.starter.LogoutTokenClaimNames;
import com.vaadin.sso.starter.OidcLogoutTokenValidator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OidcLogoutTokenValidatorTest {

    private static final String CLIENT_ID = "test-client";

    private static final String ISSUER_URI = "http://issuer.com";

    private static final String TOKEN_URI = ISSUER_URI + "/token";

    private ClientRegistration clientRegistration;

    private OidcLogoutTokenValidator validator;

    @BeforeEach
    public void setup() {
        // @formatter:off
        clientRegistration = ClientRegistration
                .withRegistrationId("test")
                .authorizationGrantType(
                        AuthorizationGrantType.CLIENT_CREDENTIALS)
                .clientId(CLIENT_ID)
                .issuerUri(ISSUER_URI)
                .tokenUri(TOKEN_URI)
                .build();
        // @formatter:on
        validator = new OidcLogoutTokenValidator(clientRegistration);
    }

    @Test
    public void validToken_validationSuccedes() {
        final var token = createValidLogoutToken().build();
        final var result = validator.validate(token);

        assertFalse(result.hasErrors());
    }

    @Test
    public void invalidAlgorithm_validationFails() {
        // @formatter:off
        final var token = createValidLogoutToken()
                .header("alg", "none")
                .build();
        // @formatter:on
        final var result = validator.validate(token);

        assertTrue(result.hasErrors());
    }

    @Test
    public void invalidIssuer_validationFails() {
        // @formatter:off
        final var token = createValidLogoutToken()
                .issuer("http://invalid")
                .build();
        // @formatter:on
        final var result = validator.validate(token);

        assertTrue(result.hasErrors());
    }

    @Test
    public void invalidIssuedAt_validationFails() {
        final var inTheFuture = Instant.now().plusSeconds(61);
        // @formatter:off
        final var token = createValidLogoutToken()
                .issuedAt(inTheFuture)
                .build();
        // @formatter:on
        final var result = validator.validate(token);

        assertTrue(result.hasErrors());
    }

    @Test
    public void missingSubjectAndSession_validationFails() {
        // @formatter:off
        final var token = createValidLogoutToken()
                .subject(null)
                .claim(LogoutTokenClaimNames.SID, null)
                .build();
        // @formatter:on
        final var result = validator.validate(token);

        assertTrue(result.hasErrors());
    }

    @Test
    public void missingBackChannelEvent_validationFails() {
        // @formatter:off
        final var token = createValidLogoutToken()
                .claim(LogoutTokenClaimNames.EVENTS, Map.of())
                .build();
        // @formatter:on
        final var result = validator.validate(token);

        assertTrue(result.hasErrors());
    }

    @Test
    public void nonceClaimPresent_validationFails() {
        // @formatter:off
        final var token = createValidLogoutToken()
                .claim(IdTokenClaimNames.NONCE, "nonce")
                .build();
        // @formatter:on
        final var result = validator.validate(token);

        assertTrue(result.hasErrors());
    }

    static Jwt.Builder createValidLogoutToken() {
        final var now = Instant.now();
        // @formatter:off
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .issuer(ISSUER_URI)
                .issuedAt(now)
                .audience(List.of(CLIENT_ID))
                .subject("john")
                .claim(LogoutTokenClaimNames.SID, "1234")
                .claim(LogoutTokenClaimNames.EVENTS, Map.of("http://schemas.openid.net/event/backchannel-logout", Map.of()));
        // @formatter:on
    }
}
