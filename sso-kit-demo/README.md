# SSO Kit Demo

A Vaadin application demonstrating Single Sign-On with Keycloak using SSO Kit.

## Prerequisites

- JDK 21+
- Docker (for running Keycloak)

## Running

Start Keycloak:

```bash
docker compose up -d
```

This starts a Keycloak instance on port 8180 with a pre-configured realm,
a client for the demo application, and a test user.

Start the application:

```bash
mvn -pl :sso-kit-demo
```

Open http://localhost:8080 in your browser.

## Test user

| Username | Password |
|----------|----------|
| user     | user     |

## Keycloak admin console

The Keycloak admin console is available at http://localhost:8180 with
credentials `admin` / `admin`.
