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

import org.jsoup.nodes.DataNode;
import org.springframework.stereotype.Component;

import com.vaadin.flow.internal.JsonUtils;
import com.vaadin.flow.server.VaadinServiceInitListener;

/**
 * This class is responsible for injecting the SSO data into the index.html
 */
@Component
public class BootstrapDataServiceListener implements VaadinServiceInitListener {

    static final String SCRIPT_STRING = """
            window.Hilla = window.Hilla || {};
            window.Hilla.SSO = JSON.parse("%s");
            """;

    private final SingleSignOnContext singleSignOnContext;

    public BootstrapDataServiceListener(
            SingleSignOnContext singleSignOnContext) {
        this.singleSignOnContext = singleSignOnContext;
    }

    @Override
    public void serviceInit(com.vaadin.flow.server.ServiceInitEvent event) {
        event.addIndexHtmlRequestListener(indexHtmlResponse -> {
            var data = singleSignOnContext.getSingleSignOnData();
            var script = SCRIPT_STRING.formatted(quotesEscaped(json(data)));

            // Use DataNode() instead of text() to avoid escaping the script
            var scriptNode = indexHtmlResponse.getDocument()
                    .createElement("script").appendChild(new DataNode(script));
            indexHtmlResponse.getDocument().body().appendChild(scriptNode);
        });
    }

    private String json(Object o) {
        if (o == null) {
            return "null";
        }
        return JsonUtils.beanToJson(o).toJson();
    }

    private String quotesEscaped(String s) {
        return s.replace("\"", "\\\"");
    }
}
