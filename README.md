# Vaadin SSO Kit

SSO Kit is an add-on for Vaadin Flow that provides all the configuration you need to add single sign-on capabilities to your applications.

SSO Kit is built upon the [OpenID Connect](https://openid.net/specs/openid-connect-core-1_0.html) specification and it comes with a [Spring Boot](https://spring.io/projects/spring-boot) starter module that takes care of configuring the security settings you need to authenticate against your identity provider.

These are the currently supported identity providers:

- [Keycloak](https://www.keycloak.org/)
- [Okta](https://okta.com/)
- [Azure Active Directory](https://azure.microsoft.com/en-us/services/active-directory/)

SSO Kit is compatible with [Vaadin Platform](https://vaadin.com/) starting from version [23.2.0](https://github.com/vaadin/platform/releases/tag/23.2.0).

## Getting Started

To get started with SSO Kit you just need to add the `sso-kit-starter` module as a dependency to your Vaadin application, e.g. in your `pom.xml`:

```xml
<dependency>
    <groupId>com.vaadin</groupId>
    <artifactId>sso-kit-starter</artifactId>
</dependency>
```

### Setting Client Credentials and Login Route

Then you need to set your identity provider client credentials in your `application.yml`, e.g. for Keycloak:

```yaml
spring:
  security:
    oauth2:
      client:
        provider:
          keycloak: # This is the registration-id, can be any value
            issuer-uri: https://my-keycloak.io/realms/my-realm
        registration:
          keycloak: # This should be the same as the registration-id
            client-id: my-client
            client-secret: verySecretValue
            scope:
            - profile
            - openid
            - email
            - roles
vaadin:
  sso:
    login-route: /oauth2/authorization/keycloak # /oauth2/authorization/<registration-id>
```

This configuration will redirect to the provider's login page any unauthorized request.

### Protecting Your Views

You can set which views require authentication annotating them as described in [Annotating the View Classes](https://vaadin.com/docs/latest/security/enabling-security/#annotating-the-view-classes). For example:

```java
@PermitAll
@Route(value = "private")
public class PrivateView extends VerticalLayout {
    // ...
}
```

### Get the Authenticated User

The SSO Kit starter provides the `AuthenticationContext` bean that you can inject into your views to get the currently authenticated user:

```java
@PermitAll
@Route(value = "private")
public class PrivateView extends VerticalLayout {

    public PrivateView(AuthenticationContext authContext) {
        authContext.getAuthenticatedUser().ifPresent(user -> {
            var fullName = user.getFullName();
            Notification.show("Hello, " + fullName + "!");
        });
    }
}
```

### Logging the User Out

The same `AuthenticationContext` bean provides the `logout()` method to terminate both the local user session and the provider's session:

```java
@PermitAll
@Route(value = "private")
public class PrivateView extends VerticalLayout {

    public PrivateView(AuthenticationContext authContext) {
        add(new Button("Logout", e -> authContext.logout()));
    }
}
```
