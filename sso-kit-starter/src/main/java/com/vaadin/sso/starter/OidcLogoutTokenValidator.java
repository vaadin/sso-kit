package com.vaadin.sso.starter;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * An {@link OAuth2TokenValidator} responsible for validating the claims in a
 * Logout Token.
 *
 * @author Vaadin Ltd
 * @since 1.0
 * @see https://openid.net/specs/openid-connect-backchannel-1_0.html#Validation
 */
public final class OidcLogoutTokenValidator
        implements OAuth2TokenValidator<Jwt> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(OidcLogoutTokenValidator.class);

    private static final Duration DEFAULT_CLOCK_SKEW = Duration.ofSeconds(60);

    private static final String ID_TOKEN_SIGNED_RESPONSE_ALG = "id_token_signed_response_alg";

    private static final String ALG_HEADER = "alg";

    private static final String ALG_RS256 = "RS256";

    private static final String BC_LOGOUT_EVENT = "http://schemas.openid.net/event/backchannel-logout";

    private final ClientRegistration clientRegistration;

    private Duration clockSkew = DEFAULT_CLOCK_SKEW;

    private Clock clock = Clock.systemUTC();

    public OidcLogoutTokenValidator(ClientRegistration clientRegistration) {
        Assert.notNull(clientRegistration, "clientRegistration cannot be null");
        this.clientRegistration = clientRegistration;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        // Logout Token Validation
        // https://openid.net/specs/openid-connect-backchannel-1_0.html#Validation

        final var invalidClaims = validateRequiredClaims(token);

        // Fail early if required claims are missing.
        if (!invalidClaims.isEmpty()) {
            LOGGER.warn("Logout token validation failed because of missing "
                    + "claims: {}", invalidClaims);
            return OAuth2TokenValidatorResult
                    .failure(invalidLogoutTokenClaims(invalidClaims));
        }

        final var providerDetails = clientRegistration.getProviderDetails();
        final var configuration = providerDetails.getConfigurationMetadata();

        // 3. Validate the ALG (algorithm) Header Parameter in the same way it
        // is validated for ID Tokens.
        final var alg = (String) token.getHeaders().get(ALG_HEADER);
        if (!Objects.equals(alg, configuration
                .getOrDefault(ID_TOKEN_SIGNED_RESPONSE_ALG, ALG_RS256))) {
            return OAuth2TokenValidatorResult
                    .failure(invalidLogoutTokenAlgorithm(alg));
        }

        // 4. Validate the ISS, AUD, and IAT Claims in the same way they are
        // validated in ID Tokens.

        // The Issuer Identifier for the OpenID Provider (which is typically
        // obtained during Discovery) MUST exactly match the value of the ISS
        // (issuer) Claim.
        final var iss = providerDetails.getIssuerUri();
        if (!Objects.equals(iss, token.getIssuer().toExternalForm())) {
            invalidClaims.put(IdTokenClaimNames.ISS, token.getIssuer());
        }

        // The Client MUST validate that the AUD (audience) Claim contains its
        // client_id value registered at the Issuer identified by the ISS
        // (issuer) Claim as an audience.
        if (!token.getAudience().contains(clientRegistration.getClientId())) {
            invalidClaims.put(IdTokenClaimNames.AUD, token.getAudience());
        }

        final var now = Instant.now(clock);

        // The IAT Claim can be used to reject tokens that were issued too
        // far away from the current time, limiting the amount of time that
        // nonces need to be stored to prevent attacks.
        if (now.plus(clockSkew).isBefore(token.getIssuedAt())) {
            invalidClaims.put(IdTokenClaimNames.IAT, token.getIssuedAt());
        }

        // 5. Verify that the Logout Token contains a SUB Claim, a SID Claim, or
        // both.
        final var subject = token.getSubject();
        final var sid = token.getClaimAsString(LogoutTokenClaimNames.SID);
        if (subject == null && sid == null) {
            invalidClaims.put(IdTokenClaimNames.SUB, subject);
            invalidClaims.put(LogoutTokenClaimNames.SID, sid);
        }

        // 6. Verify that the Logout Token contains an EVENTS Claim whose value
        // is JSON object containing the member name
        // http://schemas.openid.net/event/backchannel-logout.
        final var events = token.getClaimAsMap(LogoutTokenClaimNames.EVENTS);
        if (events == null || !events.containsKey(BC_LOGOUT_EVENT)) {
            invalidClaims.put(LogoutTokenClaimNames.EVENTS, events);
        }

        // 7. Verify that the Logout Token does not contain a nonce Claim.
        final var nonce = token.getClaim(IdTokenClaimNames.NONCE);
        if (nonce != null) {
            invalidClaims.put(IdTokenClaimNames.NONCE, nonce);
        }

        // 8. Optionally verify that another Logout Token with the same JTI
        // value has not been recently received.
        // TODO

        // 9. Optionally verify that the IDD Logout Token Claim matches the ISS
        // Claim in an ID Token issued for the current session or a recent
        // session of this RP with the OP.
        // TODO

        // 10. Optionally verify that any SUB Logout Token Claim matches the SUB
        // Claim in an ID Token issued for the current session or a recent
        // session of this RP with the OP.
        // TODO

        // 11. Optionally verify that any SID Logout Token Claim matches the SID
        // Claim in an ID Token issued for the current session or a recent
        // session of this RP with the OP.
        // TODO

        if (!invalidClaims.isEmpty()) {
            LOGGER.warn("Logout token validation failed because of invalid "
                    + "claims: {}", invalidClaims);
            return OAuth2TokenValidatorResult
                    .failure(invalidLogoutTokenClaims(invalidClaims));
        } else {
            LOGGER.debug("Logout token validation succeded");
            return OAuth2TokenValidatorResult.success();
        }
    }

    /**
     * Sets the maximum acceptable clock skew. The default is 60 seconds. The
     * clock skew is used when validating the {@link JwtClaimNames#EXP exp} and
     * {@link JwtClaimNames#IAT iat} claims.
     *
     * @param clockSkew
     *            the maximum acceptable clock skew
     */
    public void setClockSkew(Duration clockSkew) {
        Assert.notNull(clockSkew, "clockSkew cannot be null");
        Assert.isTrue(clockSkew.getSeconds() >= 0, "clockSkew must be >= 0");
        this.clockSkew = clockSkew;
    }

    /**
     * Sets the {@link Clock} used in {@link Instant#now(Clock)} when validating
     * the {@link JwtClaimNames#EXP exp} and {@link JwtClaimNames#IAT iat}
     * claims.
     *
     * @param clock
     *            the clock
     */
    public void setClock(Clock clock) {
        Assert.notNull(clock, "clock cannot be null");
        this.clock = clock;
    }

    private static OAuth2Error invalidLogoutTokenAlgorithm(String alg) {
        return new OAuth2Error("invalid_request",
                "The Logout Token algorithm is invalid: " + alg,
                "https://openid.net/specs/openid-connect-backchannel-1_0.html#Validation");
    }

    private static OAuth2Error invalidLogoutTokenClaims(
            Map<String, Object> invalidClaims) {
        return new OAuth2Error("invalid_request",
                "The Logout Token contains invalid claims: " + invalidClaims,
                "https://openid.net/specs/openid-connect-backchannel-1_0.html#Validation");
    }

    private static Map<String, Object> validateRequiredClaims(Jwt token) {
        final var requiredClaims = new HashMap<String, Object>();
        final var issuer = token.getIssuer();
        if (issuer == null) {
            requiredClaims.put(IdTokenClaimNames.ISS, issuer);
        }
        final var issuedAt = token.getIssuedAt();
        if (issuedAt == null) {
            requiredClaims.put(IdTokenClaimNames.IAT, issuedAt);
        }
        final var audience = token.getAudience();
        if (CollectionUtils.isEmpty(audience)) {
            requiredClaims.put(IdTokenClaimNames.AUD, audience);
        }
        return requiredClaims;
    }
}
