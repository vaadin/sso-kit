# @hilla/sso-kit-client-react

The **SSO Kit Client React** is the client side library for the **[SSO Kit for Hilla](https://github.com/vaadin/sso-kit/tree/main/sso-kit-starter-hilla)** and depends on it.

Getting Started with the **SSO Kit for Hilla** using React [documentation](https://hilla.dev/docs/react/acceleration-kits/sso-kit/getting-started/#frontend).

## Installation

Install the library for Hilla using React:

```sh
npm i @hilla/sso-kit-client-react@2.1.0
```

Once installed, you can import the `SingleSignOnContext` class in your application:

```js
import singleSignOnContext from "@hilla/sso-kit-client-react/SingleSignOnContext.js";
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