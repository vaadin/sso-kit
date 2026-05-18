/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */
package com.vaadin.sso.core;

import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtTypeValidator;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests that the decoder configuration used by
 * {@link BackChannelLogoutFilter}'s public constructor correctly accepts OpenID
 * Connect Back-Channel Logout tokens with {@code typ=logout+jwt} and rejects
 * tokens carrying a different type.
 */
class BackChannelLogoutDecoderTest {

    private static final String ISSUER_URI = "http://issuer.com";

    private static final String CLIENT_ID = "test-client";

    @Test
    void decoderWithLogoutJwtType_acceptsLogoutToken() throws Exception {
        final var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        final var keyPair = keyPairGenerator.generateKeyPair();
        final var publicKey = (RSAPublicKey) keyPair.getPublic();
        final var privateKey = (RSAPrivateKey) keyPair.getPrivate();

        final var token = buildSignedJwt(privateKey, "logout+jwt");

        final var typeValidator = new JwtTypeValidator("logout+jwt");
        typeValidator.setAllowEmpty(true);
        final var decoder = NimbusJwtDecoder.withPublicKey(publicKey)
                .validateType(false).build();
        decoder.setJwtValidator(
                JwtValidators.createDefaultWithValidators(typeValidator));

        final var jwt = decoder.decode(token);

        assertThat(jwt.getSubject()).isEqualTo("john");
    }

    @Test
    void decoderWithLogoutJwtType_rejectsJwtTypedToken() throws Exception {
        final var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        final var keyPair = keyPairGenerator.generateKeyPair();
        final var publicKey = (RSAPublicKey) keyPair.getPublic();
        final var privateKey = (RSAPrivateKey) keyPair.getPrivate();

        // A regular JWT token (typ=JWT) must not be accepted as a logout token
        final var token = buildSignedJwt(privateKey, "JWT");

        final var typeValidator = new JwtTypeValidator("logout+jwt");
        typeValidator.setAllowEmpty(true);
        final var decoder = NimbusJwtDecoder.withPublicKey(publicKey)
                .validateType(false).build();
        decoder.setJwtValidator(
                JwtValidators.createDefaultWithValidators(typeValidator));

        assertThatThrownBy(() -> decoder.decode(token))
                .isInstanceOf(JwtValidationException.class)
                .hasMessageContaining("logout+jwt");
    }

    private static String buildSignedJwt(RSAPrivateKey privateKey, String typ)
            throws Exception {
        final var header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(new JOSEObjectType(typ)).build();
        final var claimsSet = new JWTClaimsSet.Builder().issuer(ISSUER_URI)
                .audience(CLIENT_ID).issueTime(Date.from(Instant.now()))
                .subject("john").build();
        final var signedJwt = new SignedJWT(header, claimsSet);
        signedJwt.sign(new RSASSASigner(privateKey));
        return signedJwt.serialize();
    }
}
