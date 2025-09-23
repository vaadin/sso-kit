/*-
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.sso.starter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.pro.licensechecker.BuildType;
import com.vaadin.pro.licensechecker.Capability;
import com.vaadin.pro.licensechecker.LicenseChecker;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LicenseCheckerServiceInitListenerTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private VaadinService service;

    private MockedStatic<LicenseChecker> licenseChecker;

    @BeforeEach
    public void setup() {
        licenseChecker = mockStatic(LicenseChecker.class);
    }

    @AfterEach
    public void cleanup() {
        licenseChecker.close();
    }

    @Test
    public void developmentMode_licenseIsCheckedRuntime() {
        when(service.getDeploymentConfiguration().isProductionMode())
                .thenReturn(false);

        final var version = getProperties().getProperty(
                LicenseCheckerServiceInitListener.VERSION_PROPERTY);

        // Assert version is in X.Y format
        assertThat(version, matchesPattern("^\\d\\.\\d.*"));

        final var listener = new LicenseCheckerServiceInitListener();
        listener.serviceInit(new ServiceInitEvent(service));

        // Verify the license is checked
        BuildType buildType = null;
        licenseChecker.verify(() -> LicenseChecker.checkLicense(
                eq(LicenseCheckerServiceInitListener.PRODUCT_NAME), eq(version),
                argThat(cap -> cap.has(Capability.PRE_TRIAL)), eq(buildType)));
    }

    @Test
    public void productionMode_licenseIsNotCheckedRuntime() {
        when(service.getDeploymentConfiguration().isProductionMode())
                .thenReturn(true);

        final var listener = new LicenseCheckerServiceInitListener();
        listener.serviceInit(new ServiceInitEvent(service));

        licenseChecker.verifyNoInteractions();
    }

    @Test
    public void staticInitializer_throwsException_whenPropertiesMissing() {
        // Create a class loader that hides the sso-kit.properties resource
        ClassLoader parent = LicenseCheckerServiceInitListenerTest.class
                .getClassLoader();
        String targetClassName = "com.vaadin.sso.starter.LicenseCheckerServiceInitListener";
        String targetClassPath = targetClassName.replace('.', '/') + ".class";

        // Load the class bytes from the parent so we can define it in our
        // custom loader
        byte[] classBytes;
        try (InputStream in = parent.getResourceAsStream(targetClassPath)) {
            Assertions.assertNotNull(in,
                    "Test setup failure: could not find class bytes for "
                            + targetClassPath);
            classBytes = in.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        class PropertiesFailingClassLoader extends ClassLoader {
            PropertiesFailingClassLoader(ClassLoader parent) {
                super(parent);
            }

            @Override
            public InputStream getResourceAsStream(String name) {
                if ("sso-kit.properties".equals(name)) {
                    return new InputStream() {
                        @Override
                        public int read() throws IOException {
                            throw new IOException("Something went wrong");
                        }
                    };
                }
                return getParent().getResourceAsStream(name);
            }

            @Override
            protected Class<?> loadClass(String name, boolean resolve)
                    throws ClassNotFoundException {
                // Define the target class in this loader so its
                // getClassLoader() is this loader
                if (name.equals(targetClassName)) {
                    synchronized (getClassLoadingLock(name)) {
                        Class<?> c = findLoadedClass(name);
                        if (c == null) {
                            c = defineClass(name, classBytes, 0,
                                    classBytes.length);
                        }
                        if (resolve) {
                            resolveClass(c);
                        }
                        return c;
                    }
                }
                return super.loadClass(name, resolve);
            }
        }

        ClassLoader hidingLoader = new PropertiesFailingClassLoader(parent);

        assertThrows(ExceptionInInitializerError.class,
                () -> Class.forName(targetClassName, true, hidingLoader));
    }

    private Properties getProperties() {
        try {
            return PropertiesLoaderUtils.loadAllProperties(
                    LicenseCheckerServiceInitListener.PROPERTIES_RESOURCE);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
