package com.vaadin.auth.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.vaadin.flow.component.page.AppShellConfigurator;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class AuthDemoApplication extends SpringBootServletInitializer
        implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(AuthDemoApplication.class, args);
    }
}
