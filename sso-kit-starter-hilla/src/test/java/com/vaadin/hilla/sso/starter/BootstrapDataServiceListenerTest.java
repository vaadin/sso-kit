/*-
 * Copyright (C) 2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.hilla.sso.starter;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.communication.IndexHtmlRequestListener;
import com.vaadin.flow.server.communication.IndexHtmlResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BootstrapDataServiceListenerTest {

    private BootstrapDataServiceListener bootstrapDataServiceListener;

    @Mock
    private SingleSignOnContext singleSignOnContext;

    @Captor
    private ArgumentCaptor<IndexHtmlRequestListener> listenerArgumentCaptor;

    @BeforeEach
    void setUp() {
        bootstrapDataServiceListener = new BootstrapDataServiceListener(
                singleSignOnContext);
    }

    @Test
    public void serviceInit_indexHtmlRequestListenerIsAdded() {
        var serviceInitEvent = mock(
                com.vaadin.flow.server.ServiceInitEvent.class);

        bootstrapDataServiceListener.serviceInit(serviceInitEvent);

        verify(serviceInitEvent).addIndexHtmlRequestListener(any());
    }

    @Test
    public void serviceInit_scriptAddedToIndexHtmlResponse_withExpectedData() {
        var serviceInitEvent = mock(
                com.vaadin.flow.server.ServiceInitEvent.class);
        var vaadinRequest = mock(VaadinRequest.class);
        var vaadinResponse = mock(VaadinResponse.class);
        var document = new Document("baseUri");
        var indexHtmlResponse = new IndexHtmlResponse(vaadinRequest,
                vaadinResponse, document);
        var singleSignOnData = createSingleSignOnData();
        var expectedSingleSignOnData = "{\"authenticated\":true,\"roles\":[],\"loginLink\":\"login\",\"logoutLink\":\"logout\",\"backChannelLogoutEnabled\":true}";

        when(singleSignOnContext.getSingleSignOnData())
                .thenReturn(singleSignOnData);

        bootstrapDataServiceListener.serviceInit(serviceInitEvent);

        verify(serviceInitEvent)
                .addIndexHtmlRequestListener(listenerArgumentCaptor.capture());
        var indexHtmlRequestListener = listenerArgumentCaptor.getValue();
        indexHtmlRequestListener.modifyIndexHtmlResponse(indexHtmlResponse);
        var scriptString = indexHtmlResponse.getDocument().body()
                .getElementsByTag("script").get(0).childNodes().get(0)
                .toString();

        assertEquals(BootstrapDataServiceListener.SCRIPT_STRING
                .formatted(expectedSingleSignOnData), scriptString);
    }

    @Test
    public void serviceInit_scriptAddedToIndexHtmlResponse_withNullData() {
        var serviceInitEvent = mock(
                com.vaadin.flow.server.ServiceInitEvent.class);
        var vaadinRequest = mock(VaadinRequest.class);
        var vaadinResponse = mock(VaadinResponse.class);
        var document = new Document("baseUri");
        var indexHtmlResponse = new IndexHtmlResponse(vaadinRequest,
                vaadinResponse, document);
        var expectedSingleSignOnData = "null";

        when(singleSignOnContext.getSingleSignOnData()).thenReturn(null);

        bootstrapDataServiceListener.serviceInit(serviceInitEvent);

        verify(serviceInitEvent)
                .addIndexHtmlRequestListener(listenerArgumentCaptor.capture());
        var indexHtmlRequestListener = listenerArgumentCaptor.getValue();
        indexHtmlRequestListener.modifyIndexHtmlResponse(indexHtmlResponse);
        var scriptString = indexHtmlResponse.getDocument().body()
                .getElementsByTag("script").get(0).childNodes().get(0)
                .toString();

        assertEquals(BootstrapDataServiceListener.SCRIPT_STRING
                .formatted(expectedSingleSignOnData), scriptString);
    }

    private SingleSignOnData createSingleSignOnData() {
        var singleSignOnData = new SingleSignOnData();
        singleSignOnData.setAuthenticated(true);
        singleSignOnData.setLoginLink("login");
        singleSignOnData.setLogoutLink("logout");
        singleSignOnData.setBackChannelLogoutEnabled(true);
        return singleSignOnData;
    }
}
