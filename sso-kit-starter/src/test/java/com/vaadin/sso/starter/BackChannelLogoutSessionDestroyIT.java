/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full license.
 */
package com.vaadin.sso.starter;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.oidc.session.InMemoryOidcSessionRegistry;
import org.springframework.security.oauth2.client.oidc.session.OidcSessionInformation;
import org.springframework.security.oauth2.client.oidc.session.OidcSessionRegistry;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Verifies the contract that motivated deprecating the kit's
 * {@code BackChannelLogoutFilter} in favour of Spring Security's built-in
 * {@code oidcLogout().backChannel(...)}: when an OIDC Provider sends a
 * Back-Channel Logout request, Vaadin's {@code SessionDestroyEvent} fires for
 * the affected session.
 *
 * <p>
 * The test uses a real Tomcat (so the {@code HttpSessionListener} pipeline,
 * Spring's internal logout HTTP loopback, and Vaadin's
 * {@code HttpSessionBindingListener} on {@code VaadinSession} all run for
 * real). Only the OIDC Provider is stubbed: a tiny {@link HttpServer} serves a
 * JWKS endpoint with a public key that matches the in-test private key used to
 * sign the logout token. JWT validation runs against that JWKS exactly as it
 * would against Keycloak.
 *
 * <p>
 * Failure modes this guards against:
 * <ul>
 * <li>{@code SingleSignOnConfiguration} stops wiring
 * {@code oidcLogout().backChannel(...)} on the security filter chain;</li>
 * <li>A Spring Security upgrade changes the internal logout loopback in a way
 * that no longer invalidates the {@code HttpSession};</li>
 * <li>Vaadin's session listener registration regresses such that
 * {@code SessionDestroyEvent} no longer fires on session invalidation.</li>
 * </ul>
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = BackChannelLogoutSessionDestroyIT.TestApp.class)
class BackChannelLogoutSessionDestroyIT {

    private static final String REGISTRATION_ID = "keycloak";

    private static final String CLIENT_ID = "test-client";

    private static final String ISSUER = "https://test.local/issuer";

    private static HttpServer jwksServer;

    private static RSAKey signingKey;

    @LocalServerPort
    private int appPort;

    @Autowired
    private OidcSessionRegistry oidcSessionRegistry;

    @Autowired
    private CapturedSessionDestroyEvents captured;

    private final HttpClient http = HttpClient.newHttpClient();

    @BeforeAll
    static void startStubbedJwksServer() throws Exception {
        signingKey = new RSAKeyGenerator(2048).keyID("test-key")
                .algorithm(JWSAlgorithm.RS256).keyUse(KeyUse.SIGNATURE)
                .generate();

        byte[] jwksBody = new JWKSet(signingKey.toPublicJWK()).toString()
                .getBytes(StandardCharsets.UTF_8);

        jwksServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0),
                0);
        jwksServer.createContext("/jwks", exchange -> {
            exchange.getResponseHeaders().set("Content-Type",
                    "application/json");
            exchange.sendResponseHeaders(200, jwksBody.length);
            exchange.getResponseBody().write(jwksBody);
            exchange.close();
        });
        jwksServer.start();
    }

    @AfterAll
    static void stopStubbedJwksServer() {
        if (jwksServer != null) {
            jwksServer.stop(0);
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        // Satisfies @ConditionalOnOAuth2ClientRegistrationProperties so the
        // kit's auto-config runs. The actual ClientRegistration is built by
        // the @Bean below — Spring Boot's autoconfig backs off because we
        // provide our own ClientRegistrationRepository.
        registry.add("spring.security.oauth2.client.registration."
                + REGISTRATION_ID + ".client-id", () -> CLIENT_ID);
        registry.add("test.jwks-uri", () -> "http://localhost:"
                + jwksServer.getAddress().getPort() + "/jwks");
        // Production mode skips the dev-mode init that needs Node.js, so the
        // test boots without a local frontend bundle.
        registry.add("vaadin.productionMode", () -> "true");
    }

    @Test
    void springBackChannelLogout_firesVaadinSessionDestroyEvent()
            throws Exception {
        // 1. Hit a Vaadin route to bootstrap an HTTP session with a real
        // VaadinSession bound to it. This is what a logged-in user's session
        // looks like as far as Vaadin's session-listener wiring is concerned.
        HttpResponse<String> bootstrap = http.send(HttpRequest
                .newBuilder(URI.create(
                        "http://localhost:" + appPort + "/" + TestRoute.PATH))
                .GET().build(), BodyHandlers.ofString());
        String jsessionId = extractJsessionId(bootstrap);
        assertThat(jsessionId).as("JSESSIONID issued by bootstrap request")
                .isNotBlank();

        // 2. Pretend OIDC login already happened: register the session with
        // Spring's OidcSessionRegistry under a known sid. Spring's BC logout
        // handler looks sessions up by (issuer, audience, sid).
        String sid = "sid-" + UUID.randomUUID();
        oidcSessionRegistry.saveSessionInformation(new OidcSessionInformation(
                jsessionId, Map.of(), buildOidcUser(sid)));

        // 3. Forge a logout token (signed by our test key, served via the
        // stubbed JWKS) that targets the same sid.
        String logoutToken = signLogoutToken(sid);

        // 4. POST it to the BC logout endpoint, exactly as Keycloak would.
        HttpResponse<String> bc = http.send(HttpRequest
                .newBuilder(URI.create("http://localhost:" + appPort
                        + "/logout/connect/back-channel/" + REGISTRATION_ID))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(BodyPublishers.ofString("logout_token=" + logoutToken))
                .build(), BodyHandlers.ofString());
        assertThat(bc.statusCode())
                .as("BC logout responded 2xx; body=%s", bc.body())
                .isBetween(200, 299);

        // 5. Spring's handler does an internal HTTP loopback to invalidate the
        // session, which travels through the servlet container's listeners.
        // Wait for the captured Vaadin SessionDestroyEvent.
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> assertThat(
                captured.destroyedHttpSessionIds)
                .as("Vaadin SessionDestroyEvent fired for the logged-out session")
                .contains(jsessionId));
    }

    private DefaultOidcUser buildOidcUser(String sid) {
        Instant now = Instant.now();
        Map<String, Object> claims = new HashMap<>();
        claims.put("iss", ISSUER);
        claims.put("sub", "test-user");
        claims.put("aud", List.of(CLIENT_ID));
        claims.put("sid", sid);
        claims.put("iat", now);
        claims.put("exp", now.plus(Duration.ofMinutes(5)));
        return new DefaultOidcUser(List.of(), new OidcIdToken("id-token", now,
                now.plus(Duration.ofMinutes(5)), claims));
    }

    private String signLogoutToken(String sid) throws Exception {
        Instant now = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder().issuer(ISSUER)
                .audience(CLIENT_ID).subject("test-user")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(Duration.ofMinutes(5))))
                .jwtID(UUID.randomUUID().toString()).claim("sid", sid)
                .claim("events", Map.of(
                        "http://schemas.openid.net/event/backchannel-logout",
                        Map.of()))
                .build();
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(new JOSEObjectType("logout+jwt"))
                .keyID(signingKey.getKeyID()).build(), claims);
        jwt.sign(new RSASSASigner(signingKey));
        return jwt.serialize();
    }

    private static String extractJsessionId(HttpResponse<?> response) {
        List<String> setCookies = response.headers()
                .allValues(HttpHeaders.SET_COOKIE);
        for (String cookie : setCookies) {
            if (cookie.startsWith("JSESSIONID=")) {
                int end = cookie.indexOf(';');
                return cookie.substring("JSESSIONID=".length(),
                        end == -1 ? cookie.length() : end);
            }
        }
        return null;
    }

    @SpringBootApplication
    static class TestApp {

        // Spring's oidcLogout() configurer otherwise instantiates this
        // internally as a shared object; we expose it as a bean so the test
        // can prime it with a known session before triggering BC logout.
        @Bean
        OidcSessionRegistry oidcSessionRegistry() {
            return new InMemoryOidcSessionRegistry();
        }

        @Bean
        ClientRegistrationRepository clientRegistrationRepository(
                @Value("${test.jwks-uri}") String jwksUri) {
            ClientRegistration kc = ClientRegistration
                    .withRegistrationId(REGISTRATION_ID).clientId(CLIENT_ID)
                    .clientSecret("test-secret")
                    .clientAuthenticationMethod(
                            ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(
                            AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                    .scope("openid")
                    .authorizationUri(ISSUER + "/protocol/openid-connect/auth")
                    .tokenUri(ISSUER + "/protocol/openid-connect/token")
                    .userInfoUri(ISSUER + "/protocol/openid-connect/userinfo")
                    .userNameAttributeName("sub").issuerUri(ISSUER)
                    .jwkSetUri(jwksUri).build();
            return new InMemoryClientRegistrationRepository(kc);
        }

        @Bean
        CapturedSessionDestroyEvents capturedSessionDestroyEvents() {
            return new CapturedSessionDestroyEvents();
        }

        @Bean
        com.vaadin.flow.server.VaadinServiceInitListener captureSessionDestroy(
                CapturedSessionDestroyEvents captured) {
            return event -> event.getSource()
                    .addSessionDestroyListener(destroy -> {
                        var wrapped = destroy.getSession().getSession();
                        if (wrapped != null) {
                            captured.destroyedHttpSessionIds
                                    .add(wrapped.getId());
                        }
                    });
        }
    }

    static class CapturedSessionDestroyEvents {
        final Set<String> destroyedHttpSessionIds = ConcurrentHashMap
                .newKeySet();
    }

    @Route(TestRoute.PATH)
    @AnonymousAllowed
    @Tag("div")
    public static class TestRoute extends Component {

        static final String PATH = "test-bc-logout-route";

        public TestRoute() {
            getElement().setText("ok");
        }
    }
}
