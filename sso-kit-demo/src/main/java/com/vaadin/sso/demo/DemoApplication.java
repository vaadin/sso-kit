package com.vaadin.sso.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;

/**
 * The entry point of the Spring Boot application.
 */
@Theme("sso-kit-demo")
@NpmPackage(value = "line-awesome", version = "1.3.0")
@SpringBootApplication
public class DemoApplication extends SpringBootServletInitializer
        implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
