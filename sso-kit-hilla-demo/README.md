# SSO Kit Hilla Demo

This demo shows how to use the SSO Kit in a Hilla Lit application.

## Getting started

This guide explains how to add both authentication and SSO Kit to an existing Hilla application, in a step-by-step way, but you can also start from this application which is part of this repository and already includes all these changes. Just remember to change the parent POM if you want to copy that application out.

### Create a Hilla application without authentication

Create a Hilla application using this command:

```bash
npx @hilla/cli init hilla-sso
```

### Add dependencies

Add the `sso-kit-starter` module and the other required dependencies to the `pom.xml` of your Vaadin application:

```xml
<dependency>
    <groupId>com.vaadin</groupId>
    <artifactId>sso-kit-starter</artifactId>
    <version>2.0-SNAPSHOT</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Then add authentication to your application as explained in the [relevant section of the Hilla documentation](https://hilla.dev/docs/lit/guides/security). While all the details are explained there, let's walk the necessary steps in sequence.

### Add a Hilla Endpoint

An [AuthEndpoint](sso-kit-hilla-starter/src/main/java/dev/hilla/sso/endpoint/AuthEndpoint.java) is already included in the kit. To use it, you must [enable the new Hilla multi-module engine](https://hilla.dev/docs/lit/reference/configuration/#java-compiler-options). The easiest way to enable it is to create (or modify) the `src/main/resources/vaadin-featureflags.properties` file and add this line:

```properties
com.vaadin.experimental.hillaEngine=true
```

Otherwise, or if you want to customize the returned data, copy the [whole package](https://github.com/vaadin/sso-kit-hilla/tree/main/sso-kit-hilla-starter/src/main/java/dev/hilla/sso/endpoint) into your application and modify it.

Unless you use the same package name as for your application (by default it is `com.example.application` in generated Hilla projects), you have to whitelist your package in Spring Boot for Hilla to be able to find the Endpoint. Open your `Application.java` and add the package to the annotation:

```java
@SpringBootApplication(scanBasePackages = {
  "com.example.application", // Application package
  "dev.hilla.sso" // SSO Kit
})
public class Application ...
```

### Protect the Endpoint

Hilla allows fine-grained authorization on Endpoints and Endpoint methods. You can use annotations like `@PermitAll` or `@RolesAllowed(...)` to declare who can access what.

To try this feature, replace the `@AnonymousAllowed` annotation in `HelloWorldEndpoint.java` with `@PermitAll`, so that unauthenticated users will be unable to access the whole Endpoint. You could also apply the same annotation at method level.

### Configure the SSO provider in Spring

As a provider is needed, let's suppose you have a local Keycloak running on your machine on port 8081. Get a realm name, a client name, and the client secret and add those values to your `application.properties` file:

```properties
spring.security.oauth2.client.provider.keycloak.issuer-uri=http://localhost:8081/realms/your-realm
spring.security.oauth2.client.registration.keycloak.client-id=your-client
spring.security.oauth2.client.registration.keycloak.client-secret=your-secret
spring.security.oauth2.client.registration.keycloak.scope=profile,openid,email,roles
```

### Use the Endpoint

Start the application using the `./mvnw` command (`.\mvnw` on Windows), so that Hilla generates TypeScript files.

To complement the enpoint, the SSO Kit provides a TypeScript library that you can use in your project. It will be released as an NPM module. In the meanwhile, just copy the `sso-kit.ts` file somewhere in your project.

### Add access control to existing routes

As the `HelloWorldEndpoint` is now only accessible to registered users, it makes sense to also protect the view that uses it. They can be found in the `frontend/routes.ts` file.

To protect a view, replace its `component` parameter with `action: ssoKit.protectedView('component-name')`. So, for example, the `hello` view becomes:

```typescript
{
  path: 'hello',
  action: ssoKit.protectedView('hello-world-view'),
  icon: 'la la-globe',
  title: 'Hello World',
},
```

Add a `login` route to the exported routes (pay attention to add it to `routes` and not to `views`):

```typescript
{
  path: 'login',
  icon: '',
  title: 'Login',
  action: ssoKit.loginView(),
},
```

### Add login and logout to the interface

Open `frontend/views/main-layout.ts` and add a login/logout button in the `footer`:

```html
<footer slot="drawer">
${ssoKit.user
  ? html`
      <div className="flex items-center gap-m">
        ${ssoKit.user.fullName}
      </div>
      <vaadin-button @click="${ssoKit.logoutFromApp}">Sign out</vaadin-button>
    `
  : html`<a router-ignore href="${ssoKit.mainLoginUrl}">Sign in</a>`
}
</footer>
```

Filter out protected views from the menu by modifying the `getMenuRoutes` function:

```typescript
private getMenuRoutes(): RouteInfo[] {
  return views.filter((route) => route.title).filter((route) => !ssoKit.isForbidden(route)) as RouteInfo[];
}
```

Try to customize your views further, for example to change the root view to not use `hello-world`, which is protected, or to add a new view.

Now test the application: log in, log out, and try to use the Endpoint by clicking on the "Say hello" button in both cases.

## Add support for Back-Channel Logout

Back-Channel Logout is a feature that enables the provider to close user sessions from outside the application. For example, it can be done from the provider’s user dashboard or from another application.

### Enable the feature in the application

Go back to your `application.properties` file and add the following one:

```properties
vaadin.sso.back-channel-logout=true
```

Enable Push support to be able to get logout notifications from the server in real time by adding this line to `vaadin-featureflags.properties`:

```properties
com.vaadin.experimental.hillaPush=true
```

Restart your application to enable Push support.

### Modify the client application

Go to `main-layout.ts` and add a Confirm Dialog to notify the user, just above the empty `slot`:

```typescript
import '@vaadin/confirm-dialog';
```

```html
<vaadin-confirm-dialog
  header="Logged out"
  cancel-button-visible
  @confirm="${ssoKit.relogin}"
  @cancel="${ssoKit.logoutFromProvider}"
  .opened="${ssoKit.backChannelLogoutHappened}"
>
  <p>You have been logged out. Do you want to log in again?</p>
</vaadin-confirm-dialog>
```

To test this functionality, you need to log into the application, then close your session externally, for example from the Keycloak administration console.
