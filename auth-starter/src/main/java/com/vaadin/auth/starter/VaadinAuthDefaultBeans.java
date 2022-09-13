package com.vaadin.auth.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

/**
 * This configuration class provides default instances for the required beans.
 *
 * @author Vaadin Ltd
 * @since 1.0
 */
@Configuration
public class VaadinAuthDefaultBeans {

    /**
     * Provides a default {@link SessionRegistry} bean.
     *
     * @return the session registry bean
     */
    @Bean
    @ConditionalOnMissingBean
    public SessionRegistry getSessionRegistry() {
        return new SessionRegistryImpl();
    }
}
