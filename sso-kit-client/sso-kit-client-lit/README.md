# @hilla/sso-kit-client-lit

The SSO Kit Client is the client side dependency for the SSO Kit for Hilla. It depends on the [sso-kit-starter-hilla](https://github.com/vaadin/sso-kit/tree/main/sso-kit-starter-hilla).

Getting Started with the SSO Kit for Hilla using Lit [documentation](https://hilla.dev/docs/lit/acceleration-kits/sso-kit/getting-started/#frontend).

## Installation

Install the client:

```sh
npm i @hilla/sso-kit-client-lit@2.1.0
```

Once installed, you can import the `SingleSignOnContext` class in your application:

```js
import { singleSignOnContext as sso} from '@hilla/sso-kit-client-lit/SingleSignOnContext.js';
```

## Development

For the available scripts check the [package.json](./package.json) file.

Install npm packages:

```sh
npm install
```

Build the client:

```sh
npm run build
```

Publish the package to your registry:

```sh
npm publish --registry <your-registry>
```
