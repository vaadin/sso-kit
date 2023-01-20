import '@vaadin/button';
import '@vaadin/notification';
import { Notification } from '@vaadin/notification';
import '@vaadin/text-field';
import * as HelloWorldEndpoint from 'Frontend/generated/HelloWorldEndpoint';
import { ssoKit } from 'Frontend/kit/sso-kit';
import { html } from 'lit';
import { customElement } from 'lit/decorators.js';
import { View } from '../../views/view';

@customElement('hello-world-view')
export class HelloWorldView extends View {
  name = '';

  connectedCallback() {
    super.connectedCallback();
  }

  render() {
    return html`
      <div class='p-m gap-m'>
        <h3>${ssoKit.user!.fullName}</h3>
        <p>Username: ${ssoKit.user!.preferredUsername}</p>
        <p>Full name: ${ssoKit.user!.fullName}</p>
        <p>Email: ${ssoKit.user!.email}</p>
        <p>Roles: ${ssoKit.user!.roles?.join(', ')}</p>
      </div>

      <div class='flex p-m gap-m items-end'>
        <vaadin-text-field label="Your name" @value-changed=${this.nameChanged}></vaadin-text-field>
        <vaadin-button @click=${this.sayHello}>Say hello</vaadin-button>
      </div>
    `;
  }

  nameChanged(e: CustomEvent) {
    this.name = e.detail.value;
  }

  async sayHello() {
    try {
      const serverResponse = await HelloWorldEndpoint.sayHello(this.name);
      Notification.show(serverResponse, { duration: 2000 });
    } catch (error: any) {
      Notification.show(error.message, { duration: 2000, theme: 'error' });
    }
  }
}
