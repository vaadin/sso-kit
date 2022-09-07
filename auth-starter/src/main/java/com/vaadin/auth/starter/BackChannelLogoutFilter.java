package com.vaadin.auth.starter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.core.log.LogMessage;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class BackChannelLogoutFilter extends GenericFilterBean {

    static final String REGISTRATION_ID_URI_VARIABLE_NAME = "registrationId";

    private static final String LOG_MESSAGE = "Did not match request to %s";

    /* Value defined by the specification */
    private static final String TOKEN_PARAM_NAME = "logout_token";

    /* Value defined by the specification */
    private static final String SID_CLAIM = "sid";

    private final SessionRegistry sessionRegistry;

    private final ClientRegistrationRepository clientRegistrationRepository;

    private RequestMatcher requestMatcher = new AntPathRequestMatcher(
            VaadinAuthProperties.DEFAULT_BACKCHANNEL_LOGOUT_ROUTE);

    public BackChannelLogoutFilter(SessionRegistry sessionRegistry,
            ClientRegistrationRepository clientRegistrationRepository) {
        this.sessionRegistry = sessionRegistry;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        final var httpRequest = (HttpServletRequest) request;
        final var httpResponse = (HttpServletResponse) response;

        if (requiresLogout(httpRequest, httpResponse)) {
            performLogout(httpRequest, httpResponse);
        } else {
            chain.doFilter(request, response);
        }
    }

    private void performLogout(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {

        logger.debug("Matching Back-Channel logout request");

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

        final var issuerUri = clientRegistrationRepository
                .findByRegistrationId(clientRegistrationId).getProviderDetails()
                .getIssuerUri();
        final var decoder = JwtDecoders.fromOidcIssuerLocation(issuerUri);
        final var token = request.getParameter(TOKEN_PARAM_NAME);

        if (token == null) {
            logger.warn("Back-Channel logout request missing parameter: "
                    + TOKEN_PARAM_NAME);
            // Set the response status to 400 Bad Request as per specification
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // TODO: Token MUST be validated as described in
        // "2.6. Logout Token Validation"
        // https://openid.net/specs/openid-connect-backchannel-1_0.html
        final var jwt = decoder.decode(token);

        logger.debug("JWT claims: " + jwt.getClaims());

        final var sid = jwt.getClaimAsString(SID_CLAIM);

        sessionRegistry.getAllPrincipals().stream().filter(principal -> {
            if (principal instanceof OidcUser) {
                final var user = (OidcUser) principal;
                return sid.equals(user.getClaimAsString(SID_CLAIM));
            } else {
                return false;
            }
        }).flatMap(p -> sessionRegistry.getAllSessions(p, false).stream()).peek(
                session -> logger.debug("Matching session set to expire now: "
                        + session.getSessionId()))
                .forEach(SessionInformation::expireNow);

        // Set the response status to 200 OK as per specification
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private boolean requiresLogout(HttpServletRequest request,
            HttpServletResponse response) {
        if (requestMatcher.matches(request)) {
            return true;
        }
        if (logger.isTraceEnabled()) {
            logger.trace(LogMessage.format(LOG_MESSAGE, this.requestMatcher));
        }
        return false;
    }

    public RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }

    public void setRequestMatcher(RequestMatcher logoutRequestMatcher) {
        Assert.notNull(logoutRequestMatcher,
                "logoutRequestMatcher cannot be null");
        requestMatcher = logoutRequestMatcher;
    }

    public void setBackChannelLogoutRoute(String backchannelLogoutRoute) {
        setRequestMatcher(new AntPathRequestMatcher(backchannelLogoutRoute));
    }
}
