import { html } from 'lit';
import { customElement } from 'lit/decorators.js';
import { View } from '../../views/view';

@customElement('about-view')
export class AboutView extends View {
  render() {
    return html`<div>
      <img style="width: 200px;" src="images/empty-plant.png" />
      <h2>This view is accessible to everyone</h2>
    </div>`;
  }

  connectedCallback() {
    super.connectedCallback();
    this.classList.add(
      'flex',
      'flex-col',
      'h-full',
      'items-center',
      'justify-center',
      'p-l',
      'text-center',
      'box-border'
    );
  }
}
