/*-
 * Copyright (C) 2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.sso.starter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.web.session.SessionInformationExpiredEvent;

import com.vaadin.flow.shared.ApplicationConstants;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UidlExpiredSessionStrategyTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HttpServletRequest request;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HttpServletResponse response;

    private SessionInformation session;

    private SessionInformationExpiredEvent event;

    private UidlExpiredSessionStrategy strategy;

    @BeforeEach
    public void setup() {
        session = new SessionInformation("principal", "1234",
                Date.from(Instant.now()));
        event = new SessionInformationExpiredEvent(session, request, response);
        strategy = new UidlExpiredSessionStrategy();

        when(request.getContextPath()).thenReturn("");
        when(request.getHttpServletMapping().getPattern()).thenReturn("/");
    }

    @Test
    public void isInternalRequest_setVaadinRefreshToken()
            throws IOException, ServletException {
        when(request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER))
                .thenReturn(ApplicationConstants.REQUEST_TYPE_UIDL);

        strategy.onExpiredSessionDetected(event);

        verify(response.getWriter()).write("Vaadin-Refresh: /");
    }

    @Test
    public void isExternalRequest_setRedirectStatusAndHeader()
            throws IOException, ServletException {

        strategy.onExpiredSessionDetected(event);

        verify(response).setStatus(302);
        verify(response).setHeader("Location", "/");
    }
}
