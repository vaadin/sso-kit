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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

/**
 * A filter responsible to handle OpenID Connect Back-Channel Logout requests.
 *
 * @author Vaadin Ltd
 * @since 1.0
 * @see https://openid.net/specs/openid-connect-backchannel-1_0.html
 * @deprecated Use built-in Spring Security support for OpenID Connect
 *             Back-Channel Logout
 */
@Deprecated(since = "3.1", forRemoval = true)
public class BackChannelLogoutFilter extends GenericFilterBean {

    /* Value defined by the specification */
    static final String TOKEN_PARAM_NAME = "logout_token";

    static final String REGISTRATION_ID_URI_VARIABLE_NAME = "registrationId";

    private static final String LOG_MESSAGE = "Did not match request to %s";

    private final SessionRegistry sessionRegistry;

    private final ClientRegistrationRepository clientRegistrationRepository;

    private final JwtDecoderFactory<ClientRegistration> decoderFactory;

    private RequestMatcher requestMatcher = PathPatternRequestMatcher
            .withDefaults().matcher(
                    AbstractSingleSignOnProperties.DEFAULT_BACKCHANNEL_LOGOUT_ROUTE);

    private final ApplicationEventPublisher eventPublisher;

    /**
     * Creates an instance of the filter.
     *
     * @param sessionRegistry
     *            the session registry, {@code not null}
     * @param clientRegistrationRepository
     *            the client-registration repository, {@code not null}
     * @param eventPublisher
     *            the event-publisher to publish logout events, {@code not null}
     */
    public BackChannelLogoutFilter(SessionRegistry sessionRegistry,
            ClientRegistrationRepository clientRegistrationRepository,
            ApplicationEventPublisher eventPublisher) {
        this(sessionRegistry, clientRegistrationRepository, eventPublisher,
                clientRegistration -> {
                    final var issuerUri = clientRegistration
                            .getProviderDetails().getIssuerUri();
                    return JwtDecoders.fromOidcIssuerLocation(issuerUri);
                });
    }

    BackChannelLogoutFilter(SessionRegistry sessionRegistry,
            ClientRegistrationRepository clientRegistrationRepository,
            ApplicationEventPublisher eventPublisher,
            JwtDecoderFactory<ClientRegistration> decoderFactory) {
        this.sessionRegistry = Objects.requireNonNull(sessionRegistry);
        this.clientRegistrationRepository = Objects
                .requireNonNull(clientRegistrationRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.decoderFactory = Objects.requireNonNull(decoderFactory);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        final var httpRequest = (HttpServletRequest) request;
        final var httpResponse = (HttpServletResponse) response;

        if (requiresLogout(httpRequest)) {
            logger.debug("Matching Back-Channel logout request");
            performLogout(httpRequest, httpResponse);
        }
        chain.doFilter(request, response);
    }

    private void performLogout(HttpServletRequest request,
            HttpServletResponse response) throws JwtValidationException {
        final var clientRegistrationId = requestMatcher.matcher(request)
                .getVariables().get(REGISTRATION_ID_URI_VARIABLE_NAME);

        if (clientRegistrationId == null) {
            logger.warn("Back-Channel logout request matcher missing "
                    + "required registrationId URI variable:"
                    + REGISTRATION_ID_URI_VARIABLE_NAME);
            // Set the response status to 400 Bad Request as per specification
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        final var clientRegistration = clientRegistrationRepository
                .findByRegistrationId(clientRegistrationId);

        if (clientRegistration == null) {
            logger.warn(
                    "Client registration not found: " + clientRegistrationId);
            // Set the response status to 400 Bad Request as per specification
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        final var token = request.getParameter(TOKEN_PARAM_NAME);

        if (token == null) {
            logger.warn("Back-Channel logout request missing parameter: "
                    + TOKEN_PARAM_NAME);
            // Set the response status to 400 Bad Request as per specification
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        final var decoder = decoderFactory.createDecoder(clientRegistration);
        final var jwt = decoder.decode(token);

        // TODO: Validation should be part of the decoding process. This means
        // that we need a custom JwtDecoderFactory that creates a decoder for
        // logout tokens and validates them with OidcLogoutTokenValidator
        // See Spring's OidcIdTokenDecoderFactory and OidcIdTokenValidator
        final var tokenValidator = new OidcLogoutTokenValidator(
                clientRegistration);
        if (tokenValidator.validate(jwt).hasErrors()) {
            logger.warn("Invalid logout token");
            // Set the response status to 400 Bad Request as per specification
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        final var tokenSub = jwt.getSubject();
        final var tokenSid = jwt.getClaimAsString(LogoutTokenClaimNames.SID);

        sessionRegistry.getAllPrincipals().stream().filter(principal -> {
            if (principal instanceof OidcUser) {
                final var user = (OidcUser) principal;
                // If a SID claim is provided, use it to match the principal;
                // otherwise use the SUB claim (not null in valid tokens)
                if (tokenSid != null) {
                    final var userSid = user
                            .getClaimAsString(LogoutTokenClaimNames.SID);
                    return Objects.equals(tokenSid, userSid);
                } else {
                    final var userSub = user.getSubject();
                    return Objects.equals(tokenSub, userSub);
                }
            } else {
                return false;
            }
        }).peek(p -> eventPublisher.publishEvent(new UserLogoutEvent(p)))
                .flatMap(p -> sessionRegistry.getAllSessions(p, false).stream())
                .forEach(SessionInformation::expireNow);

        // Set the response status to 200 OK as per specification
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private boolean requiresLogout(HttpServletRequest request) {
        if (requestMatcher.matches(request)) {
            return true;
        }
        if (logger.isTraceEnabled()) {
            logger.trace(LogMessage.format(LOG_MESSAGE, requestMatcher));
        }
        return false;
    }

    /**
     * Gets the request-matcher configured for this filter.
     *
     * @return this filter's request-matcher, not {@code null}
     */
    public RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }

    /**
     * Sets the request-matcher for this filter.
     *
     * @param logoutRequestMatcher
     *            the request-matcher, not {@code null}
     */
    public void setRequestMatcher(RequestMatcher logoutRequestMatcher) {
        requestMatcher = Objects.requireNonNull(logoutRequestMatcher);
    }

    /**
     * Sets the back-channel logout route to match for this filter to handle the
     * requests.
     *
     * @param backChannelLogoutRoute
     *            the route to match, not {@code null}
     */
    public void setBackChannelLogoutRoute(String backChannelLogoutRoute) {
        Objects.requireNonNull(backChannelLogoutRoute);
        setRequestMatcher(PathPatternRequestMatcher.withDefaults()
                .matcher(HttpMethod.POST, backChannelLogoutRoute));
    }
}
