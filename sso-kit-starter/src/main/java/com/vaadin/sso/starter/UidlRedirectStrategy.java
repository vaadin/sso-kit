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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.security.web.DefaultRedirectStrategy;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.HandlerHelper;

/**
 * A strategy to handle redirects which is aware of UIDL requests.
 *
 * @author Vaadin Ltd
 * @since 1.0
 */
public class UidlRedirectStrategy extends DefaultRedirectStrategy {

    @Override
    public void sendRedirect(HttpServletRequest request,
            HttpServletResponse response, String url) throws IOException {
        final var servletMapping = request.getHttpServletMapping().getPattern();
        if (HandlerHelper.isFrameworkInternalRequest(servletMapping, request)) {
            UI.getCurrent().getPage().setLocation(url);
        } else {
            super.sendRedirect(request, response, url);
        }
    }
}
