# @vaadin/sso-kit-client-lit

The **SSO Kit Client Lit** is the client side library for the **[SSO Kit for Hilla](https://github.com/vaadin/sso-kit/tree/main/sso-kit-starter-hilla)** and depends on it.

Getting Started with the **SSO Kit for Hilla** with Lit [documentation](https://hilla.dev/docs/lit/acceleration-kits/sso-kit/getting-started/#frontend).

## Installation

Install the library for Hilla with Lit:

```sh
npm i @vaadin/sso-kit-client-lit
```

Once installed, you can import the `SingleSignOnContext` class in your application:

```js
import ssoContext from "@vaadin/sso-kit-client-lit";
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

Pack the library locally:

```sh
npm pack
```

Publish the library to your registry:

```sh
npm publish --registry <your-registry>
```
