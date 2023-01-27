package dev.hilla.sso.endpoint;

import java.util.List;

import com.vaadin.flow.server.auth.AnonymousAllowed;

import dev.hilla.Endpoint;
import dev.hilla.Nonnull;
import dev.hilla.sso.starter.SingleSignOnContext;
import dev.hilla.sso.starter.SingleSignOnData;

@Endpoint
@AnonymousAllowed
public class SingleSignOnEndpoint {

    private final SingleSignOnContext singleSignOnContext;

    public SingleSignOnEndpoint(SingleSignOnContext singleSignOnContext) {
        this.singleSignOnContext = singleSignOnContext;
    }

    @Nonnull
    public SingleSignOnData fetchAll() {
        return singleSignOnContext.getSingleSignOnData();
    }

    @Nonnull
    public List<@Nonnull String> getRegisteredProviders() {
        return singleSignOnContext.getRegisteredProviders();
    }
}
