import '@vaadin/button';
import '@vaadin/notification';
import { Notification } from '@vaadin/notification';
import '@vaadin/text-field';
import User from 'Frontend/generated/dev/hilla/sso/endpoint/User';
import { HelloWorldEndpoint, UserEndpoint } from 'Frontend/generated/endpoints';
import { html } from 'lit';
import { customElement, property } from 'lit/decorators.js';
import { View } from '../../views/view';

@customElement('hello-world-view')
export class HelloWorldView extends View {
  name = '';

  @property()
  private user: User | undefined;

  async connectedCallback() {
    super.connectedCallback();
    this.user = await UserEndpoint.getAuthenticatedUser();
  }

  render() {
    return html`
      <div class='p-m gap-m'>
        <h3>${this.user?.fullName}</h3>
        <p>Username: ${this.user?.preferredUsername}</p>
        <p>Full name: ${this.user?.fullName}</p>
        <p>Email: ${this.user?.email}</p>
        <p>Roles: ${this.user?.roles.join(', ')}</p>
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
