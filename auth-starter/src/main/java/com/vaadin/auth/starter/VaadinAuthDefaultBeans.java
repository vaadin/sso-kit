package com.vaadin.auth.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

@Configuration
public class VaadinAuthDefaultBeans {

    @Bean
    @ConditionalOnMissingBean
    public SessionRegistry getSessionRegistry() {
        return new SessionRegistryImpl();
    }
}
