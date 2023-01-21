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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.vaadin.sso.starter.SingleSignOnProperties;

import dev.hilla.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Flux;

/**
 * Contains utility methods and information related to single sign-on.
 */
@Component
public class SingleSignOnContext {

    private final ClientRegistrationRepository clientRegistrationRepository;

    private final SingleSignOnProperties properties;

    private final BackChannelLogoutSubscription backChannelLogoutSubscription;

    public SingleSignOnContext(
            ClientRegistrationRepository clientRegistrationRepository,
            SingleSignOnProperties properties,
            BackChannelLogoutSubscription backChannelLogoutSubscription) {
        Objects.requireNonNull(clientRegistrationRepository);
        Objects.requireNonNull(properties);
        Objects.requireNonNull(backChannelLogoutSubscription);
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.properties = properties;
        this.backChannelLogoutSubscription = backChannelLogoutSubscription;
    }

    /**
     * Conveniently get the current OIDC user from context.
     *
     * @return the OidcUser as an Optional, or null if the user is not
     *         authenticated or is not a OIDC user
     */
    public static Optional<OidcUser> getOidcUser() {
        // Use the Optional pattern to walk the security context and get the
        // OIDC user
        return Optional.of(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .filter(OidcUser.class::isInstance).map(OidcUser.class::cast);
    }

    /**
     * Get the current HTTP request from context.
     *
     * @return the current HTTP request as an Optional, or null if the request
     *         is not available in the context.
     */
    static Optional<HttpServletRequest> getCurrentHttpRequest() {
        // Use the Optional pattern to walk the request context and get the HTTP
        // request
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
    }

    /**
     * Returns all the registered OAuth2 providers.
     *
     * @return a list of identifiers of the registered OAuth2 providers, as
     *         defined in the application properties.
     */
    public List<@Nonnull String> getRegisteredProviders() {
        // Use the Optional pattern to walk the client registration repository
        // down to the client registrations
        return Optional.of(clientRegistrationRepository)
                // By default, the client registration repository is an instance
                // of InMemoryClientRegistrationRepository
                .filter(InMemoryClientRegistrationRepository.class::isInstance)
                .map(InMemoryClientRegistrationRepository.class::cast)
                .map(repo -> {
                    List<String> list = new ArrayList<>();
                    repo.iterator().forEachRemaining(registration -> list
                            .add(registration.getRegistrationId()));
                    return list;
                }).orElse(List.of());
    }

    /**
     * Exposes the configuration property which determines whether the
     * back-channel logout is enabled.
     *
     * @return true if the back-channel logout is enabled, false otherwise.
     */
    public boolean isBackChannelLogoutEnabled() {
        return properties.isBackChannelLogout();
    }

    /**
     * Returns the URL of the back-channel logout endpoint. This must be called
     * by the client to finalize the logout procedure.
     *
     * @return the URL of the back-channel logout endpoint as an Optional.
     */
    public Optional<String> getLogoutUrl() {
        // Use the Optional pattern to walk the security context and get the
        // authentication token
        return Optional.of(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(OAuth2AuthenticationToken.class::isInstance)
                .map(OAuth2AuthenticationToken.class::cast)
                // build the URL from the token
                .map(token -> buildLogoutUrl(token));
    }

    private String buildLogoutUrl(
            OAuth2AuthenticationToken authenticationToken) {
        // Build the logout URL according to the OpenID Connect specification
        var registrationId = authenticationToken
                .getAuthorizedClientRegistrationId();
        var clientRegistration = clientRegistrationRepository
                .findByRegistrationId(registrationId);
        var details = clientRegistration.getProviderDetails();
        // The end_session_endpoint is buried in the provider metadata
        var endSessionEndpoint = details.getConfigurationMetadata()
                .get("end_session_endpoint").toString();
        var user = (OidcUser) authenticationToken.getPrincipal();
        var builder = UriComponentsBuilder.fromUriString(endSessionEndpoint)
                .queryParam("id_token_hint", user.getIdToken().getTokenValue())
                .queryParam("post_logout_redirect_uri",
                        getPostLogoutRedirectUri());
        return builder.toUriString();
    }

    private String getPostLogoutRedirectUri() {
        var logoutRedirectRoute = properties.getLogoutRedirectRoute();
        String logoutUri;

        // the logout redirect route can contain a {baseUrl} placeholder
        if (logoutRedirectRoute.contains("{baseUrl}")) {
            logoutUri = getCurrentHttpRequest()
                    .map(request -> UriComponentsBuilder
                            .fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                            .replacePath(request.getContextPath())
                            .replaceQuery(null).fragment(null).build()
                            .toUriString())
                    .map(uri -> logoutRedirectRoute.replace("{baseUrl}", uri))
                    .orElse(logoutRedirectRoute);
        } else {
            logoutUri = logoutRedirectRoute;
        }
        return logoutUri;
    }

    /**
     * Returns the Flux of back-channel logout messages for the current user.
     *
     * @return a flux which is a filter of the global flux.
     */
    public Flux<BackChannelLogoutSubscription.Message> getBackChannelLogoutFlux() {
        var principal = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return backChannelLogoutSubscription.getFluxForUser(principal);
    }

    public SingleSignOnData getSingleSignOnData() {
        SingleSignOnData data = new SingleSignOnData();
        data.setRegisteredProviders(getRegisteredProviders());

        SingleSignOnContext.getOidcUser().ifPresent(u -> {
            data.setUser(User.from(u));
            data.setLogoutUrl(getLogoutUrl().orElseThrow());
            data.setBackChannelLogoutEnabled(isBackChannelLogoutEnabled());
        });

        return data;
    }
}
