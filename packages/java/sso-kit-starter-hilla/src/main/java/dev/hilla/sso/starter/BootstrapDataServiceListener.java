/*-
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package dev.hilla.sso.starter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.nodes.DataNode;

import com.vaadin.flow.server.VaadinServiceInitListener;

/**
 * This class is responsible for injecting the single sign-on data into the
 * index.html
 */
public class BootstrapDataServiceListener implements VaadinServiceInitListener {

    static final String SCRIPT_STRING = """
            window.Vaadin = window.Vaadin || {};
            window.Vaadin.SingleSignOnData = %s;
            """;

    private final SingleSignOnContext singleSignOnContext;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public BootstrapDataServiceListener(
            SingleSignOnContext singleSignOnContext) {
        this.singleSignOnContext = singleSignOnContext;
    }

    @Override
    public void serviceInit(com.vaadin.flow.server.ServiceInitEvent event) {
        event.addIndexHtmlRequestListener(indexHtmlResponse -> {
            var data = singleSignOnContext.getSingleSignOnData();
            var script = SCRIPT_STRING.formatted(json(data));

            // Use DataNode() instead of text() to avoid escaping the script
            var scriptNode = indexHtmlResponse.getDocument()
                    .createElement("script").appendChild(new DataNode(script));
            indexHtmlResponse.getDocument().body().appendChild(scriptNode);
        });
    }

    private String json(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException | NullPointerException e) {
            return "undefined";
        }
    }
}
