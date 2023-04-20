# @hilla/sso-kit-client

The **SSO Kit Client** is the client side library for the **[SSO Kit for Hilla](https://github.com/vaadin/sso-kit/tree/main/sso-kit-starter-hilla)** and depends on it.

Getting Started with the **SSO Kit for Hilla** using Lit [documentation](https://hilla.dev/docs/lit/acceleration-kits/sso-kit/getting-started/#frontend).

Getting Started with the **SSO Kit for Hilla** using React [documentation](https://hilla.dev/docs/react/acceleration-kits/sso-kit/getting-started/#frontend).

The library consists three packages. The **[SSO Kit Client Lit](lit)**, **[SSO Kit Client React](react)** and **[SSO Kit Client Core](core)** which is private, so it is not published into public registry. However, its sources are included into other two libraries during building, packaging and publishing.

## Installation

Install one of the libraries for Hilla using Lit or Hilla using React:

```sh
npm i @hilla/sso-kit-client-lit@2.1.0
```
```sh
npm i @hilla/sso-kit-client-react@2.1.0
```

Once installed, you can import the `SingleSignOnContext` class in your application:

```js
import singleSignOnContext from "@hilla/sso-kit-client/SingleSignOnContext.js";
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
