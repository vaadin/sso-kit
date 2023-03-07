package dev.hilla.sso.starter.endpoint;

import java.util.List;

import dev.hilla.sso.starter.SingleSignOnContext;
import dev.hilla.sso.starter.SingleSignOnData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SingleSignOnEndpointTest {

    @Test
    public void fetchAll_returnsTheExpectedSingleSignOnData() {
        var expectedSingleSignOnData = new SingleSignOnData();
        var singleSignOnContext = mock(SingleSignOnContext.class);

        when(singleSignOnContext.getSingleSignOnData())
                .thenReturn(expectedSingleSignOnData);

        var singleSignOnEndpoint = new SingleSignOnEndpoint(
                singleSignOnContext);
        var singleSignOnData = singleSignOnEndpoint.fetchAll();

        assertEquals(expectedSingleSignOnData, singleSignOnData);
    }

    @Test
    public void getRegisteredProviders_returnsTheExpectedProviders() {
        var expectedProviders = List.of("provider1", "provider2");
        var singleSignOnContext = mock(SingleSignOnContext.class);

        when(singleSignOnContext.getRegisteredProviders())
                .thenReturn(expectedProviders);

        var singleSignOnEndpoint = new SingleSignOnEndpoint(
                singleSignOnContext);
        var providers = singleSignOnEndpoint.getRegisteredProviders();

        assertEquals(providers, expectedProviders);
        assertEquals(providers.get(0), expectedProviders.get(0));
        assertEquals(providers.get(1), expectedProviders.get(1));
    }
}
