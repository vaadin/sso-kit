package dev.hilla.sso.starter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import com.vaadin.flow.spring.SpringBootAutoConfiguration;
import com.vaadin.flow.spring.SpringSecurityAutoConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class SingleSignOnDefaultBeansTest {

    private WebApplicationContextRunner contextRunner;

    @Mock
    private ClientRegistrationRepository clientRegistrationRepository;

    @BeforeEach
    public void init() {
        contextRunner = new WebApplicationContextRunner()
                .withConfiguration(
                        AutoConfigurations.of(SpringBootAutoConfiguration.class,
                                SpringSecurityAutoConfiguration.class,
                                SingleSignOnConfiguration.class,
                                SingleSignOnDefaultBeans.class))
                .withPropertyValues(
                        "spring.security.oauth2.client.registration.foo.client-id=foo")
                .withBean(ClientRegistrationRepository.class,
                        () -> clientRegistrationRepository);
    }

    @Test
    public void autoConfigureProperty_notSet_defaultBeansCreated() {
        contextRunner.run(ctx -> assertThat(ctx)
                .hasSingleBean(SingleSignOnDefaultBeans.class));
    }

    @Test
    public void autoConfigureProperty_isFalse_defaultBeansNotCreated() {
        contextRunner.withPropertyValues("hilla.sso.auto-configure=false")
                .run(ctx -> assertThat(ctx)
                        .doesNotHaveBean(SingleSignOnDefaultBeans.class));
    }

    @Test
    public void clientRepository_notAvailable_defaultBeansNotCreated() {
        final var runner = new WebApplicationContextRunner().withConfiguration(
                AutoConfigurations.of(SpringBootAutoConfiguration.class,
                        SpringSecurityAutoConfiguration.class,
                        SingleSignOnConfiguration.class,
                        SingleSignOnDefaultBeans.class));
        runner.run(ctx -> assertThat(ctx)
                .doesNotHaveBean(SingleSignOnDefaultBeans.class));
    }
}
