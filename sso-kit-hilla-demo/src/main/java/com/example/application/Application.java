package com.example.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication(scanBasePackages = { "com.example.application",
        "dev.hilla.sso" })
@Theme(value = "sso-kit-hilla-demo")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
