# @hilla/sso-kit-client

The **SSO Kit Client** is the client side library for the **[SSO Kit for Hilla](https://github.com/vaadin/sso-kit/tree/main/sso-kit-starter-hilla)** and depends on it.

Getting Started with the **SSO Kit for Hilla** with Lit [documentation](https://hilla.dev/docs/lit/acceleration-kits/sso-kit/getting-started/#frontend).

Getting Started with the **SSO Kit for Hilla** with React [documentation](https://hilla.dev/docs/react/acceleration-kits/sso-kit/getting-started/#frontend).

The library consists three packages. The **[SSO Kit Client Lit](lit)**, **[SSO Kit Client React](react)** and **[SSO Kit Client Core](core)** which is private, so it is not published into public registry. However, its sources are included into other two libraries during building, packaging and publishing.

## Installation

### Install the library for Hilla with Lit:

```sh
npm i @hilla/sso-kit-client-lit
```

Once installed, you can import the `SingleSignOnContext` class in your application:

```js
import ssoContext from "@hilla/sso-kit-client-lit";
```

### Install the library for Hilla with React:

```sh
npm i @hilla/sso-kit-client-react
```

Once installed, you can import the `useSsoContext` hook in your application:

```js
import { useSsoContext } from "@hilla/sso-kit-client-react";
```

## Development

For the available scripts check the [package.json](./package.json) file.

Install npm packages:

```sh
npm install
```

Build the library:

```sh
npm run build
```

Publish the library to your registry:

```sh
npm publish --registry <your-registry>
```
