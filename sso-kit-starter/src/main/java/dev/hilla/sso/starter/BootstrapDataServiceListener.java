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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Component;

import com.vaadin.flow.internal.JsonUtils;
import com.vaadin.flow.server.VaadinServiceInitListener;

import elemental.json.JsonValue;

@Component
public class BootstrapDataServiceListener implements VaadinServiceInitListener {

    private final SingleSignOnContext singleSignOnContext;

    public BootstrapDataServiceListener(
            SingleSignOnContext singleSignOnContext) {
        this.singleSignOnContext = singleSignOnContext;
    }

    @Override
    public void serviceInit(com.vaadin.flow.server.ServiceInitEvent event) {
        event.addIndexHtmlRequestListener(indexHtmlResponse -> {
            var data = singleSignOnContext.getSingleSignOnData();
            var script = """
                    window.Hilla = window.Hilla || {};
                    Hilla.BootstrapSSO = "%s";
                    """.formatted(encoded(json(data)));

            var scriptNode = indexHtmlResponse.getDocument()
                    .createElement("script").text(script);
            indexHtmlResponse.getDocument().body().appendChild(scriptNode);
        });
    }

    private String json(Object o) {
        if (o == null) {
            return "null";
        }

        JsonValue json;

        if (o instanceof List<?> list) {
            json = JsonUtils.listToJson(list);
        } else {
            json = JsonUtils.beanToJson(o);
        }

        return json.toJson();
    }

    private String encoded(String s) {
        return Base64.getEncoder()
                .encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }
}
