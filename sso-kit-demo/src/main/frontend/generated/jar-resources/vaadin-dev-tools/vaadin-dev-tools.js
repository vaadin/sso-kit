import{LitElement as e,css as t,html as n}from"lit";import{customElement as r,property as i,state as a}from"lit/decorators.js";function o(e,t,n,r){var i=arguments.length,a=i<3?t:r===null?r=Object.getOwnPropertyDescriptor(t,n):r,o;if(typeof Reflect==`object`&&typeof Reflect.decorate==`function`)a=Reflect.decorate(e,t,n,r);else for(var s=e.length-1;s>=0;s--)(o=e[s])&&(a=(i<3?o(a):i>3?o(t,n,a):o(t,n))||a);return i>3&&a&&Object.defineProperty(t,n,a),a}var s=class extends HTMLElement{#e;#t;#n;#r;#i;#a;constructor(){super(),this.#e=null,this.#n=!1,this.#r=null,this.#i=null,this.#a=null,this.#t=this.attachShadow({mode:`closed`}),this.render(),this.setupProtection()}static get observedAttributes(){return[`expired`,`start-failure`,`license-download`,`product`]}get isEnterprise(){return this.#a===`vaadin-ee`}render(){this.#t.innerHTML=`
    
      <style>
        :host {
          position: fixed;
          bottom: 0;
          left: 0;
          right: 0;
          z-index: 9999;
          min-height: 100% !important;
          min-width: 100% !important;
          display: flex !important;
          visibility: visible !important;
          opacity: 1 !important;
          clip-path: none !important;
          text-indent: 0 !important;          
          background-color: rgba(0, 0, 0, 0.5);          
        }
        
        .container {
          background: white;
          border-radius: 0.5rem;
          box-sizing: border-box;
          color: #3f4d62;
          font-family: "nb_international_pro","ui-sans-serif","system-ui","-apple-system","BlinkMacSystemFont","Segoe UI","Roboto","Helvetica Neue","Arial","Noto Sans","sans-serif","Apple Color Emoji","Segoe UI Emoji","Segoe UI Symbol","Noto Color Emoji";
          font-size: 0.875rem;
          font-weight: normal;
          letter-spacing: 0.01em;
          line-height: 1.7;
          margin: auto;
          max-width: 32rem;
          padding: 1.5rem 1.5rem 1rem 1.5rem;
        }
        
        h2 {
          color: #0d1219;
          font-size: 1.5rem;
          line-height: 1.2;
          margin: 0 0 1rem 0;
        }
        
        p {
          margin: 0;
        }
        
        span.badge {
          background: #F1F5FB;
          border-radius: 4px;
          display: inline-block;
          font-size: 0.8125rem;
          font-weight: 600;
          line-height: 1.7;
          padding: 0 6px 0 4px;
        }
        
        span.badge svg {
          vertical-align: sub;
        }
        
        p:has(+ ul) {
          color: #0d1219;
          font-weight: 600;
          margin-top: 1.25rem;
        }
        
        ul {
          display: flex;
          flex-direction: column;
          gap: 0.5rem;
          list-style: none;
          margin: 0.5rem 0 1.5rem 0;
          padding: 0;
        }
        
        ul li {
          display: flex;
          gap: 0.5rem;
        }

        ul li svg {
          flex-shrink: 0;
        }
        
        ul li span:first-of-type {
          color: #0d1219;
          font-weight: 600;
        }
        
        hr {
          border-color: rgb(224, 233, 244);
          border-top: 0;
          margin: 1.5rem 0 0.75rem 0;
        }
        
        button {
          align-items: center;
          background: #F1F5FB;
          border: none;
          border-radius: 8px;
          display: flex;
          color: #0368DE;
          font-family: "nb_international_promono","ui-monospace","SFMono-Regular","Menlo","Monaco","Consolas","Liberation Mono","Courier New","monospace";
          font-size: inherit;
          font-weight: 600;
          height: 2.375rem;
          justify-content: center;
          line-height: 1.7;
          padding: 0;
          width: 100%;
        }
        
        button.primary {
          background: #0368DE;
          color: white;
          flex-direction: column;
          height: 4.5rem;
        }
        
        button.primary span + span {
          font-size: 0.8125rem;
          font-weight: normal;
        }
        
        button.primary + button.secondary {
          margin-top: 0.5rem;
        }
        
        hr + p {
          font-size: 0.8125rem;
          line-height: 1.7;
          text-align: center;
        }
        
        a {
          color: #0368DE;
        }
        
        .error {
          background: #ffedee;
          border-radius: 0.75rem;
          display: flex;
          flex-direction: column;
          gap: 0.25rem;
          margin-top: 1.5rem;
          padding: 1rem 1.5rem;
        }
        
        .error h3 {
          color: #0d1219;
          font-size: inherit;
          line-height: inherit;
          margin: 0;
        }
        
        .error a {
          font-weight: 600;
        }
      </style>
     
    <div class='container'>
      ${this.#n?`
        <h2>Trial expired</h2>
        ${this.getExpiredIntro()}
        <p>Continue getting full access to:</p>
        ${this.getProductsList()}
        <button ${this.#i===`started`?`disabled`:``} class='primary'>
          <span>Extend trial 30 days</span>
          <span>Sign up ⋅ No credit card required</span>
        </button>
        `:`
        <h2>Get full access to all features</h2>
        ${this.getAccessIntro()}
        <p>Get full access:</p>
        ${this.getProductsList()}
        <button ${this.#i===`started`?`disabled`:``} class='primary'>
          <span>Start 7-day trial</span>
          <span>No registration or credit card required</span>
        </button>
        <button ${this.#i===`started`?`disabled`:``} class='secondary'>
          Activate your license
        </button>
        `}
      ${this.#r?`
        <div class='error'>
          <h3>Trial failed to start</h3>
          <p>Something went wrong while starting your trial. Try again in a moment. If the issue persists, <a href="https://pages.vaadin.com/contact" target="_blank">contact our support team</a>.</p>
        </div>`:``}
      ${this.#i===`started`?`<p><strong>Waiting for the license key to be downloaded...</strong></p>`:``}
      ${this.#i===`failed`?`<div class="error">Failed to download the license key. Please try again later.</div>`:``}
      <hr>
      <p>
        By starting your trial, you agree to our <a href='https://vaadin.com/commercial-license-and-service-terms' target='_blank'>terms and conditions</a>.
      </p>
    </div>
    `,this.#t.querySelector(`button.primary`)?.addEventListener(`click`,()=>{this.dispatchEvent(new CustomEvent(`primary-button-click`,{detail:{expired:this.#n}}))}),this.#t.querySelector(`button.secondary`)?.addEventListener(`click`,()=>{this.dispatchEvent(new CustomEvent(`secondary-button-click`))})}getAccessIntro(){return this.isEnterprise?`
        <p>
          This application uses <span class="badge">
          <svg width="18" height="18" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 15V9.75h3V15h-3Zm-4.5 0V3h3v12h-3ZM3 15V6.75h3V15H3Z" fill="url(#ee)"/>
          <defs>
            <linearGradient id="ee" x1="9" y1="3" x2="9" y2="15" gradientUnits="userSpaceOnUse">
              <stop stop-color="#1A81FA"/>
              <stop offset="1" stop-color="#8854FC"/>
            </linearGradient>
          </defs>
        </svg>
          Vaadin Enterprise Edition</span>. Activate a free trial to use it.
        </p>`:`
        <p>
          Vaadin Core is free and open-source. To use Pro components like <span class="badge">
          <svg width="18" height="18" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 15V9.75h3V15h-3Zm-4.5 0V3h3v12h-3ZM3 15V6.75h3V15H3Z" fill="url(#a)"/>
          <defs>
            <linearGradient id="a" x1="9" y1="3" x2="9" y2="15" gradientUnits="userSpaceOnUse">
              <stop stop-color="#1A81FA"/>
              <stop offset="1" stop-color="#8854FC"/>
            </linearGradient>
          </defs>
        </svg>
          Charts</span> in your app, activate a free trial.
        </p>`}getExpiredIntro(){return this.isEnterprise?`
        <p>
          Sign in to keep using
          <span class="badge">
            <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 15V9.75H15V15H12ZM7.5 15V3H10.5V15H7.5ZM3 15V6.75H6V15H3Z" fill="url(#eex)"/>
              <defs>
                <linearGradient id="eex" x1="9" y1="3" x2="9" y2="15" gradientUnits="userSpaceOnUse">
                  <stop stop-color="#1A81FA"/>
                  <stop offset="1" stop-color="#8854FC"/>
                </linearGradient>
              </defs>
            </svg>
            Vaadin Enterprise Edition
          </span> for 30 more days.
        </p>`:`
        <p>
          Vaadin Core is free and open-source. Sign in to keep using
          <span class="badge">
            <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
<path d="M12 15V9.75H15V15H12ZM7.5 15V3H10.5V15H7.5ZM3 15V6.75H6V15H3Z" fill="url(#paint0_linear_85_186)"/>
              <defs>
                <linearGradient id="paint0_linear_85_186" x1="9" y1="3" x2="9" y2="15" gradientUnits="userSpaceOnUse">
                  <stop stop-color="#1A81FA"/>
                  <stop offset="1" stop-color="#8854FC"/>
                </linearGradient>
              </defs>
            </svg>
            Pro components
          </span> and
          <span class="badge">
            <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M8.2125 11.6625L12.45 7.425L11.3812 6.35625L8.2125 9.525L6.6375 7.95L5.56875 9.01875L8.2125 11.6625ZM9 16.5C7.2625 16.0625 5.82812 15.0656 4.69687 13.5094C3.56562 11.9531 3 10.225 3 8.325V3.75L9 1.5L15 3.75V8.325C15 10.225 14.4344 11.9531 13.3031 13.5094C12.1719 15.0656 10.7375 16.0625 9 16.5ZM9 14.925C10.3 14.5125 11.375 13.6875 12.225 12.45C13.075 11.2125 13.5 9.8375 13.5 8.325V4.78125L9 3.09375L4.5 4.78125V8.325C4.5 9.8375 4.925 11.2125 5.775 12.45C6.625 13.6875 7.7 14.5125 9 14.925Z" fill="url(#paint0_linear_85_190)"/>
              <defs>
                <linearGradient id="paint0_linear_85_190" x1="9" y1="1.5" x2="9" y2="16.5" gradientUnits="userSpaceOnUse">
                  <stop stop-color="#1A81FA"/>
                  <stop offset="1" stop-color="#8854FC"/>
                </linearGradient>
              </defs>
            </svg>
            Team features
          </span> for 30 more days.
        </p>`}getProductsList(){return this.isEnterprise?`
        <ul>
          <li>
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none">
            <path d="M16 20v-7h4v7h-4Zm-6 0V4h4v16h-4Zm-6 0V9h4v11H4Z" fill="url(#eel)"/>
            <defs>
              <linearGradient id="eel" x1="12" y1="4" x2="12" y2="20" gradientUnits="userSpaceOnUse">
                <stop stop-color="#1A81FA" />
                <stop offset="1" stop-color="#8854FC" />
              </linearGradient>
            </defs>
          </svg>
            <p><span>Vaadin Enterprise Edition</span><br/><span>All Pro components, Team features and Kits</span></p>
          </li>
        </ul>
    `:`
        <ul>
          <li>
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none">
            <path
              d="M16 20v-7h4v7h-4Zm-6 0V4h4v16h-4Zm-6 0V9h4v11H4Z"
              fill="url(#a)"
            />
            <defs>
              <linearGradient
                id="a"
                x1="12"
                y1="4"
                x2="12"
                y2="20"
                gradientUnits="userSpaceOnUse"
              >
                <stop stop-color="#1A81FA" />
                <stop offset="1" stop-color="#8854FC" />
              </linearGradient>
            </defs>
          </svg>
            <p><span>Pro components</span><br/><span>Charts, Grid Pro, CRUD and more</span></p>
          </li>
          <li>
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none">
            <path
              d="M10.95 15.55 16.6 9.9l-1.425-1.425L10.95 12.7l-2.1-2.1-1.425 1.425 3.525 3.525ZM12 22c-2.317-.583-4.23-1.913-5.737-3.988C4.754 15.938 4 13.633 4 11.1V5l8-3 8 3v6.1c0 2.533-.754 4.838-2.262 6.912C16.229 20.087 14.317 21.418 12 22Zm0-2.1c1.733-.55 3.167-1.65 4.3-3.3s1.7-3.483 1.7-5.5V6.375l-6-2.25-6 2.25V11.1c0 2.017.567 3.85 1.7 5.5s2.567 2.75 4.3 3.3Z"
              fill="url(#a)"
            />
            <defs>
              <linearGradient
                id="a"
                x1="12"
                y1="2"
                x2="12"
                y2="22"
                gradientUnits="userSpaceOnUse"
              >
                <stop stop-color="#1A81FA" />
                <stop offset="1" stop-color="#8854FC" />
              </linearGradient>
            </defs></svg
          >
            <p><span>Team features</span><br/><span>Acceleration Kits</span></p>
          </li>
        </ul>
    `}connectedCallback(){this.setupParentRemovalProtection()}disconnectedCallback(){this.cleanup()}attributeChangedCallback(e,t,n){e===`expired`?this.handleExpiredChange(n!==null&&n!==`false`):e===`start-failure`?this.handleStartFailed(n===`expired`):e===`license-download`?this.handleLicenseDownload(n):e===`product`&&this.handleProduct(n)}handleProduct(e){this.#a!==e&&(this.#a=e,this.render())}handleLicenseDownload(e){this.#i!==e&&(this.#i=e,this.render())}handleExpiredChange(e){this.#n!==e&&(this.#n=e,this.render())}handleStartFailed(e){(this.#r!==e||this.#n!==e)&&(this.#n=e,this.#r=!0,this.render())}setupProtection(){let e=Element.prototype.remove,t=this;this.remove=function(){console.debug(`Attempt to remove vaadin-pretrial detected - restoring`);let n=t.parentNode;e.call(t),t.restoreSplashScreen(n)},this.protectStyles()}setupParentRemovalProtection(){!this.#e&&this.parentNode&&(this.#e=new MutationObserver(e=>{e.forEach(e=>{e.type===`childList`&&e.removedNodes.forEach(t=>{t===this&&(console.debug(`vaadin-pretrial removal detected - restoring`),this.restoreSplashScreen(e.target))})})}),this.#e.observe(this.parentNode,{childList:!0,subtree:!0}))}protectStyles(){Object.defineProperty(this,"style",{get(){return{}},set(e){}})}cleanup(){this.#e&&this.#e.disconnect()}restoreSplashScreen(e){e&&setTimeout(()=>{console.debug(`Re-adding vaadin-pretrial component`);let t=this.querySelector(`[slot="products"]`);e.contains(this)&&e.removeChild(this);let n=document.createElement(`vaadin-pretrial`);this.#a&&n.setAttribute(`product`,this.#a),this.#n&&n.setAttribute(`expired`,`true`),this.#r&&n.setAttribute(`start-failure`,this.#n?`expired`:``),t&&n.appendChild(t.cloneNode(!0)),e.appendChild(n)},0)}};customElements.define(`vaadin-pretrial`,s);function c(e){e.detail.expired?A():k()}function l(){A()}var u=(e,t)=>{if(!e)return;let n=t.product?.name,r=e.querySelector(`vaadin-pretrial`);if(r){n===`vaadin-ee`&&r.getAttribute(`product`)!==`vaadin-ee`&&r.setAttribute(`product`,`vaadin-ee`);return}let i=t.preTrial?.trialState===`EXPIRED`,a=document.createElement(`vaadin-pretrial`);n&&a.setAttribute(`product`,n),i&&a.setAttribute(`expired`,``),a.addEventListener(`secondary-button-click`,l),a.addEventListener(`primary-button-click`,c),e.innerHTML=`<slot></slot>`,e.appendChild(a)},d=(e,t)=>{t&&t.querySelector(`vaadin-pretrial`)?.setAttribute(`start-failure`,e?`expired`:``)},f=(e,t)=>{t&&t.querySelector(`vaadin-pretrial`)?.setAttribute(`license-download`,e)},p=1e3,m=`vaadin-license-download-completed`,h=(e,t)=>{let n=Array.from(e.querySelectorAll(t.join(`, `))),r=Array.from(e.querySelectorAll(`*`)).filter(e=>e.shadowRoot).flatMap(e=>h(e.shadowRoot,t));return[...n,...r]},g=!1,_=(e,t)=>{g||=(window.addEventListener(`message`,e=>{e.data===`validate-license`&&window.location.reload()},!1),!0);let n=e._overlayElement;if(n){if(n.shadowRoot){let e=n.shadowRoot.querySelector(`slot:not([name])`);if(e&&e.assignedElements().length>0){_(e.assignedElements()[0],t);return}}_(n,t);return}let r=t.messageHtml?t.messageHtml:`${t.message} <p>Component: ${t.product.name} ${t.product.version}</p>`.replace(/https:([^ ]*)/g,`<a href='https:$1'>https:$1</a>`);e.isConnected&&(e.outerHTML=`<no-license style="display:flex;align-items:center;text-align:center;justify-content:center;"><div>${r}</div></no-license>`)},v={},y={},b={},x={},S=e=>`${e.name}_${e.version}`,C=e=>{let{cvdlName:t,version:n}=e.constructor,r={name:t,version:n},i=e.tagName.toLowerCase();v[t]=v[t]??[],v[t].push(i);let a=b[S(r)];a&&setTimeout(()=>_(e,a),p),b[S(r)]||x[S(r)]||y[S(r)]||(y[S(r)]=!0,window.Vaadin.devTools.checkLicense(r))},w=e=>{x[S(e)]=!0,console.debug(`License check ok for`,e)},T=e=>{let t=e.product.name;b[S(e.product)]=e,console.error(`License check failed for`,t);let n=v[t];n?.length>0&&h(document,n).forEach(t=>{setTimeout(()=>_(t,b[S(e.product)]),p)})},E=e=>{let t=e.message,n=e.product.name;e.messageHtml=`No license found. <a target=_blank onclick="javascript:window.open(this.href);return false;" href="${t}">Go here to start a trial or retrieve your license.</a>`,b[S(e.product)]=e,console.error(`No license found when checking`,n);let r=v[n];r?.length>0&&h(document,r).forEach(t=>{setTimeout(()=>_(t,b[S(e.product)]),p)})},D=(e,t)=>e.command===`license-check-ok`?(w(e.data),!0):e.command===`license-check-failed`?(T(e.data),!0):e.command===`license-check-nokey`?(u(t,e.data),E(e.data),!0):e.command===`license-pretrial-started`?(console.debug(`Pre-trial period started`,e.data),window.location.reload(),!0):e.command===`license-pretrial-expired`?(console.debug(`Pre-trial period expired`,e.data),d(!0,t),!0):e.command===`license-pretrial-failed`?(console.debug(`Pre-trial period start failed`,e.data),d(!1,t),!0):e.command===`license-download-completed`?(console.debug(`License downloaded`),O(e.data),!0):e.command===`license-download-started`?(f(`started`,t),!0):e.command===`license-download-failed`?(f(`failed`,t),!0):!1,O=e=>{let t=new CustomEvent(m,{detail:e,cancelable:!0});document.dispatchEvent(t)&&window.location.reload()},k=()=>{window.Vaadin.devTools.startPreTrial()},A=e=>{let t=Object.values(b);t.length>0&&window.Vaadin.devTools.downloadLicense(t[0].product,e)},j=()=>{window.Vaadin.devTools.createdCvdlElements.forEach(e=>{C(e)}),window.Vaadin.devTools.createdCvdlElements={push:e=>{C(e)}}},M;(function(e){e.ACTIVE=`active`,e.INACTIVE=`inactive`,e.UNAVAILABLE=`unavailable`,e.ERROR=`error`})(M||={});var N=class{constructor(){this.status=M.UNAVAILABLE}static{this.HEARTBEAT_INTERVAL=18e4}onHandshake(){}onConnectionError(e){}onStatusChange(e){}setActive(e){!e&&this.status===M.ACTIVE?this.setStatus(M.INACTIVE):e&&this.status===M.INACTIVE&&this.setStatus(M.ACTIVE)}setStatus(e){this.status!==e&&(this.status=e,this.onStatusChange(e))}},P=class extends N{constructor(e){super(),this.webSocket=new WebSocket(e),this.webSocket.onmessage=e=>this.handleMessage(e),this.webSocket.onerror=e=>this.handleError(e),this.webSocket.onclose=e=>{this.status!==M.ERROR&&this.setStatus(M.UNAVAILABLE),this.webSocket=void 0},setInterval(()=>{this.webSocket&&self.status!==M.ERROR&&this.status!==M.UNAVAILABLE&&this.webSocket.send(``)},N.HEARTBEAT_INTERVAL)}onReload(e){}handleMessage(e){let t;try{t=JSON.parse(e.data)}catch(e){this.handleError(`[${e.name}: ${e.message}`);return}if(t.command===`hello`)this.setStatus(M.ACTIVE),this.onHandshake();else if(t.command===`reload`){if(this.status===M.ACTIVE){let e=t.strategy||`reload`;this.onReload(e)}}else this.handleError(`Unknown message from the livereload server: ${e}`)}handleError(e){console.error(e),this.setStatus(M.ERROR),e instanceof Event&&this.webSocket?this.onConnectionError(`Error in WebSocket connection to ${this.webSocket.url}`):this.onConnectionError(e)}},F=16384,I=class extends N{static{this.HEARTBEAT_INTERVAL=18e4}constructor(e){if(super(),this.canSend=!1,!e)return;let t={transport:`websocket`,fallbackTransport:`websocket`,url:e,contentType:`application/json; charset=UTF-8`,reconnectInterval:5e3,timeout:-1,maxReconnectOnClose:1e7,trackMessageLength:!0,enableProtocol:!0,handleOnlineOffline:!1,executeCallbackBeforeReconnect:!0,messageDelimiter:`|`,onMessage:e=>{let t={data:e.responseBody};this.handleMessage(t)},onError:e=>{this.canSend=!1,this.handleError(e)},onOpen:()=>{this.canSend=!0},onClose:()=>{this.canSend=!1},onClientTimeout:()=>{this.canSend=!1},onReconnect:()=>{this.canSend=!1},onReopen:()=>{this.canSend=!0}};R().then(e=>{this.socket=e.subscribe(t)})}onReload(e){}onUpdate(e,t){}onMessage(e){}handleMessage(e){let t;try{t=JSON.parse(e.data)}catch(e){this.handleError(`[${e.name}: ${e.message}`);return}if(t.command===`hello`)this.setStatus(M.ACTIVE),this.onHandshake();else if(t.command===`reload`){if(this.status===M.ACTIVE){let e=t.strategy||`reload`;this.onReload(e)}}else t.command===`update`?this.status===M.ACTIVE&&this.onUpdate(t.path,t.content):this.onMessage(t)}handleError(e){console.error(e),this.setStatus(M.ERROR),this.onConnectionError(e)}send(e,t){if(!this.socket||!this.canSend){L(()=>this.socket&&this.canSend,n=>this.send(e,t));return}let n=JSON.stringify({command:e,data:t}),r=n.length+`|`+n;for(;r.length;)this.socket.push(r.substring(0,F)),r=r.substring(F)}};function L(e,t){let n=e();n?t(n):setTimeout(()=>L(e,t),50)}function R(){return new Promise((e,t)=>{L(()=>window?.vaadinPush?.atmosphere,e)})}var z=1,B=`vaadin-refresh-ui`;function V(e){if(e.id)return`#`+CSS.escape(e.id);let t=[],n=e;for(;n&&n!==document.documentElement&&n!==document.body;){if(n.id){t.unshift(`#`+CSS.escape(n.id));break}let e=n.parentElement;if(!e)break;let r=1,i=n.previousElementSibling;for(;i;)i.tagName===n.tagName&&r++,i=i.previousElementSibling;t.unshift(n.tagName.toLowerCase()+`:nth-of-type(`+r+`)`),n=e}return t.length>0?t.join(` > `):``}function H(){let e=window.Vaadin;return Object.keys(e?.Flow?.clients||{}).filter(e=>e!==`TypeScript`).map(t=>e.Flow.clients[t])}function U(){let e={};return(window.scrollX!==0||window.scrollY!==0)&&(e.__window__={scrollTop:window.scrollY,scrollLeft:window.scrollX}),document.querySelectorAll(`*`).forEach(t=>{if(t.scrollTop>0||t.scrollLeft>0){let n=V(t);n&&(e[n]={scrollTop:t.scrollTop,scrollLeft:t.scrollLeft})}}),e}function W(e){let t=U();H().forEach(t=>{t.sendEventMessage&&t.sendEventMessage(z,`ui-refresh`,{fullRefresh:e})}),q(t)}var G=!1;function K(){G||(G=!0,window.addEventListener(B,e=>{W(e.detail?.fullRefresh===!0)}))}function q(e){if(Object.keys(e).length===0)return;let t=0,n=()=>{requestAnimationFrame(()=>{for(let[t,n]of Object.entries(e))if(t===`__window__`)window.scrollTo(n.scrollLeft,n.scrollTop);else{let e=document.querySelector(t);e&&(e.scrollTop=n.scrollTop,e.scrollLeft=n.scrollLeft)}})},r=()=>{let e=H();e.length>0&&e.every(e=>!e.isActive())||++t>=200?n():setTimeout(r,50)};setTimeout(r,50)}var J,Y;(function(e){e.LOG=`log`,e.INFORMATION=`information`,e.WARNING=`warning`,e.ERROR=`error`})(Y||={});var X=import.meta.hot?import.meta.hot.hmrClient:void 0,Z=class extends e{constructor(){super(...arguments),this.unhandledMessages=[],this.conf={enable:!1,url:``,contextRelativePath:``,liveReloadPort:-1},this.bodyShadowRoot=null,this.frontendStatus=M.UNAVAILABLE,this.javaStatus=M.UNAVAILABLE,this.componentPickActive=!1}static{J=this}static get styles(){return[t`
        :host {
          --dev-tools-font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen-Sans, Ubuntu, Cantarell,
            'Helvetica Neue', sans-serif;
          --dev-tools-font-family-monospace: SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New',
            monospace;

          --dev-tools-font-size: 0.8125rem;
          --dev-tools-font-size-small: 0.75rem;

          --dev-tools-text-color: rgba(255, 255, 255, 0.8);
          --dev-tools-text-color-secondary: rgba(255, 255, 255, 0.65);
          --dev-tools-text-color-emphasis: rgba(255, 255, 255, 0.95);
          --dev-tools-text-color-active: rgba(255, 255, 255, 1);

          --dev-tools-background-color-inactive: rgba(45, 45, 45, 0.25);
          --dev-tools-background-color-active: rgba(45, 45, 45, 0.98);
          --dev-tools-background-color-active-blurred: rgba(45, 45, 45, 0.85);

          --dev-tools-border-radius: 0.5rem;
          --dev-tools-box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.05), 0 4px 12px -2px rgba(0, 0, 0, 0.4);

          --dev-tools-blue-hsl: 206, 100%, 70%;
          --dev-tools-blue-color: hsl(var(--dev-tools-blue-hsl));
          --dev-tools-green-hsl: 145, 80%, 42%;
          --dev-tools-green-color: hsl(var(--dev-tools-green-hsl));
          --dev-tools-grey-hsl: 0, 0%, 50%;
          --dev-tools-grey-color: hsl(var(--dev-tools-grey-hsl));
          --dev-tools-yellow-hsl: 38, 98%, 64%;
          --dev-tools-yellow-color: hsl(var(--dev-tools-yellow-hsl));
          --dev-tools-red-hsl: 355, 100%, 68%;
          --dev-tools-red-color: hsl(var(--dev-tools-red-hsl));

          /* Needs to be in ms, used in JavaScript as well */
          --dev-tools-transition-duration: 180ms;

          all: initial;

          direction: ltr;
          cursor: default;
          font: normal 400 var(--dev-tools-font-size) / 1.125rem var(--dev-tools-font-family);
          color: var(--dev-tools-text-color);
          -webkit-user-select: none;
          -moz-user-select: none;
          user-select: none;
          color-scheme: dark;

          position: fixed;
          z-index: 20000;
          pointer-events: none;
          bottom: 0;
          right: 0;
          width: 100%;
          height: 100%;
          display: flex;
          flex-direction: column-reverse;
          align-items: flex-end;
        }

        .dev-tools {
          pointer-events: auto;
          display: flex;
          align-items: center;
          position: fixed;
          z-index: inherit;
          right: 0.5rem;
          bottom: 0.5rem;
          min-width: 1.75rem;
          height: 1.75rem;
          max-width: 1.75rem;
          border-radius: 0.5rem;
          padding: 0.375rem;
          box-sizing: border-box;
          background-color: var(--dev-tools-background-color-inactive);
          box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.05);
          color: var(--dev-tools-text-color);
          transition: var(--dev-tools-transition-duration);
          white-space: nowrap;
          line-height: 1rem;
        }

        .dev-tools:hover,
        .dev-tools.active {
          background-color: var(--dev-tools-background-color-active);
          box-shadow: var(--dev-tools-box-shadow);
        }

        .dev-tools.active {
          max-width: calc(100% - 1rem);
        }

        .dev-tools .status-description {
          overflow: hidden;
          text-overflow: ellipsis;
          padding: 0 0.25rem;
        }

        .dev-tools.error {
          background-color: hsla(var(--dev-tools-red-hsl), 0.15);
          animation: bounce 0.5s;
          animation-iteration-count: 2;
        }

        .window.hidden {
          opacity: 0;
          transform: scale(0);
          position: absolute;
        }

        .window.visible {
          transform: none;
          opacity: 1;
          pointer-events: auto;
        }

        .window.visible ~ .dev-tools {
          opacity: 0;
          pointer-events: none;
        }

        .window.visible ~ .dev-tools .dev-tools-icon,
        .window.visible ~ .dev-tools .status-blip {
          transition: none;
          opacity: 0;
        }

        .window {
          border-radius: var(--dev-tools-border-radius);
          overflow: auto;
          margin: 0.5rem;
          min-width: 30rem;
          max-width: calc(100% - 1rem);
          max-height: calc(100vh - 1rem);
          flex-shrink: 1;
          background-color: var(--dev-tools-background-color-active);
          color: var(--dev-tools-text-color);
          transition: var(--dev-tools-transition-duration);
          transform-origin: bottom right;
          display: flex;
          flex-direction: column;
          box-shadow: var(--dev-tools-box-shadow);
          outline: none;
        }

        .window-toolbar {
          display: flex;
          flex: none;
          align-items: center;
          padding: 0.375rem;
          white-space: nowrap;
          order: 1;
          background-color: rgba(0, 0, 0, 0.2);
          gap: 0.5rem;
        }

        .ahreflike {
          font-weight: 500;
          color: var(--dev-tools-text-color-secondary);
          text-decoration: underline;
          cursor: pointer;
        }

        .ahreflike:hover {
          color: var(--dev-tools-text-color-emphasis);
        }

        .button {
          all: initial;
          font-family: inherit;
          font-size: var(--dev-tools-font-size-small);
          line-height: 1;
          white-space: nowrap;
          background-color: rgba(0, 0, 0, 0.2);
          color: inherit;
          font-weight: 600;
          padding: 0.25rem 0.375rem;
          border-radius: 0.25rem;
        }

        .button:focus,
        .button:hover {
          color: var(--dev-tools-text-color-emphasis);
        }

        .message.information {
          --dev-tools-notification-color: var(--dev-tools-blue-color);
        }

        .message.warning {
          --dev-tools-notification-color: var(--dev-tools-yellow-color);
        }

        .message.error {
          --dev-tools-notification-color: var(--dev-tools-red-color);
        }

        .message {
          display: flex;
          padding: 0.1875rem 0.75rem 0.1875rem 2rem;
          background-clip: padding-box;
        }

        .message.log {
          padding-left: 0.75rem;
        }

        .message-content {
          margin-right: 0.5rem;
          -webkit-user-select: text;
          -moz-user-select: text;
          user-select: text;
        }

        .message-heading {
          position: relative;
          display: flex;
          align-items: center;
          margin: 0.125rem 0;
        }

        .message.log {
          color: var(--dev-tools-text-color-secondary);
        }

        .message:not(.log) .message-heading {
          font-weight: 500;
        }

        .message.has-details .message-heading {
          color: var(--dev-tools-text-color-emphasis);
          font-weight: 600;
        }

        .message-heading::before {
          position: absolute;
          margin-left: -1.5rem;
          display: inline-block;
          text-align: center;
          font-size: 0.875em;
          font-weight: 600;
          line-height: calc(1.25em - 2px);
          width: 14px;
          height: 14px;
          box-sizing: border-box;
          border: 1px solid transparent;
          border-radius: 50%;
        }

        .message.information .message-heading::before {
          content: 'i';
          border-color: currentColor;
          color: var(--dev-tools-notification-color);
        }

        .message.warning .message-heading::before,
        .message.error .message-heading::before {
          content: '!';
          color: var(--dev-tools-background-color-active);
          background-color: var(--dev-tools-notification-color);
        }

        .features-tray {
          padding: 0.75rem;
          flex: auto;
          overflow: auto;
          animation: fade-in var(--dev-tools-transition-duration) ease-in;
          user-select: text;
        }

        .features-tray p {
          margin-top: 0;
          color: var(--dev-tools-text-color-secondary);
        }

        .features-tray .feature {
          display: flex;
          align-items: center;
          gap: 1rem;
          padding-bottom: 0.5em;
        }

        .message .message-details {
          font-weight: 400;
          color: var(--dev-tools-text-color-secondary);
          margin: 0.25rem 0;
        }

        .message .message-details[hidden] {
          display: none;
        }

        .message .message-details p {
          display: inline;
          margin: 0;
          margin-right: 0.375em;
          word-break: break-word;
        }

        .message .persist {
          color: var(--dev-tools-text-color-secondary);
          white-space: nowrap;
          margin: 0.375rem 0;
          display: flex;
          align-items: center;
          position: relative;
          -webkit-user-select: none;
          -moz-user-select: none;
          user-select: none;
        }

        .message .persist::before {
          content: '';
          width: 1em;
          height: 1em;
          border-radius: 0.2em;
          margin-right: 0.375em;
          background-color: rgba(255, 255, 255, 0.3);
        }

        .message .persist:hover::before {
          background-color: rgba(255, 255, 255, 0.4);
        }

        .message .persist.on::before {
          background-color: rgba(255, 255, 255, 0.9);
        }

        .message .persist.on::after {
          content: '';
          order: -1;
          position: absolute;
          width: 0.75em;
          height: 0.25em;
          border: 2px solid var(--dev-tools-background-color-active);
          border-width: 0 0 2px 2px;
          transform: translate(0.05em, -0.05em) rotate(-45deg) scale(0.8, 0.9);
        }

        .message .dismiss-message {
          font-weight: 600;
          align-self: stretch;
          display: flex;
          align-items: center;
          padding: 0 0.25rem;
          margin-left: 0.5rem;
          color: var(--dev-tools-text-color-secondary);
        }

        .message .dismiss-message:hover {
          color: var(--dev-tools-text-color);
        }

        .notification-tray {
          display: flex;
          flex-direction: column-reverse;
          align-items: flex-end;
          margin: 0.5rem;
          flex: none;
        }

        .window.hidden + .notification-tray {
          margin-bottom: 3rem;
        }

        .notification-tray .message {
          pointer-events: auto;
          background-color: var(--dev-tools-background-color-active);
          color: var(--dev-tools-text-color);
          max-width: 30rem;
          box-sizing: border-box;
          border-radius: var(--dev-tools-border-radius);
          margin-top: 0.5rem;
          transition: var(--dev-tools-transition-duration);
          transform-origin: bottom right;
          animation: slideIn var(--dev-tools-transition-duration);
          box-shadow: var(--dev-tools-box-shadow);
          padding-top: 0.25rem;
          padding-bottom: 0.25rem;
        }

        .notification-tray .message.animate-out {
          animation: slideOut forwards var(--dev-tools-transition-duration);
        }

        .notification-tray .message .message-details {
          max-height: 10em;
          overflow: hidden;
        }

        .message-tray {
          flex: auto;
          overflow: auto;
          max-height: 20rem;
          user-select: text;
        }

        .message-tray .message {
          animation: fade-in var(--dev-tools-transition-duration) ease-in;
          padding-left: 2.25rem;
        }

        .message-tray .message.warning {
          background-color: hsla(var(--dev-tools-yellow-hsl), 0.09);
        }

        .message-tray .message.error {
          background-color: hsla(var(--dev-tools-red-hsl), 0.09);
        }

        .message-tray .message.error .message-heading {
          color: hsl(var(--dev-tools-red-hsl));
        }

        .message-tray .message.warning .message-heading {
          color: hsl(var(--dev-tools-yellow-hsl));
        }

        .message-tray .message + .message {
          border-top: 1px solid rgba(255, 255, 255, 0.07);
        }

        .message-tray .dismiss-message,
        .message-tray .persist {
          display: none;
        }

        @keyframes slideIn {
          from {
            transform: translateX(100%);
            opacity: 0;
          }
          to {
            transform: translateX(0%);
            opacity: 1;
          }
        }

        @keyframes slideOut {
          from {
            transform: translateX(0%);
            opacity: 1;
          }
          to {
            transform: translateX(100%);
            opacity: 0;
          }
        }

        @keyframes fade-in {
          0% {
            opacity: 0;
          }
        }

        @keyframes bounce {
          0% {
            transform: scale(0.8);
          }
          50% {
            transform: scale(1.5);
            background-color: hsla(var(--dev-tools-red-hsl), 1);
          }
          100% {
            transform: scale(1);
          }
        }

        @supports (backdrop-filter: blur(1px)) {
          .dev-tools,
          .window,
          .notification-tray .message {
            backdrop-filter: blur(8px);
          }

          .dev-tools:hover,
          .dev-tools.active,
          .window,
          .notification-tray .message {
            background-color: var(--dev-tools-background-color-active-blurred);
          }
        }
      `]}static{this.DISMISSED_NOTIFICATIONS_IN_LOCAL_STORAGE=`vaadin.live-reload.dismissedNotifications`}static{this.ACTIVE_KEY_IN_SESSION_STORAGE=`vaadin.live-reload.active`}static{this.TRIGGERED_KEY_IN_SESSION_STORAGE=`vaadin.live-reload.triggered`}static{this.TRIGGERED_COUNT_KEY_IN_SESSION_STORAGE=`vaadin.live-reload.triggeredCount`}static{this.AUTO_DEMOTE_NOTIFICATION_DELAY=5e3}static{this.HOTSWAP_AGENT=`HOTSWAP_AGENT`}static{this.JREBEL=`JREBEL`}static{this.SPRING_BOOT_DEVTOOLS=`SPRING_BOOT_DEVTOOLS`}static{this.BACKEND_DISPLAY_NAME={HOTSWAP_AGENT:`HotswapAgent`,JREBEL:`JRebel`,SPRING_BOOT_DEVTOOLS:`Spring Boot Devtools`}}static get isActive(){let e=window.sessionStorage.getItem(J.ACTIVE_KEY_IN_SESSION_STORAGE);return e===null||e!==`false`}elementTelemetry(){let e={};try{let t=localStorage.getItem(`vaadin.statistics.basket`);if(!t)return;e=JSON.parse(t)}catch{return}this.frontendConnection&&this.frontendConnection.send(`reportTelemetry`,{browserData:e})}openWebSocketConnection(){if(this.frontendStatus=M.UNAVAILABLE,this.javaStatus=M.UNAVAILABLE,!this.conf.token){console.error(`Dev tools functionality denied for this host. See Vaadin documentation on how to configure devmode.hostsAllowed property: https://vaadin.com/docs/latest/configuration/properties#properties`);return}let e=e=>console.error(e),t=(e=`reload`)=>{if(e===`refresh`||e===`full-refresh`)W(e===`full-refresh`);else{let e=U();window.sessionStorage.setItem(`vaadin-hotswap-scroll`,JSON.stringify(e));let t=window.sessionStorage.getItem(J.TRIGGERED_COUNT_KEY_IN_SESSION_STORAGE),n=t?parseInt(t,10)+1:1;window.sessionStorage.setItem(J.TRIGGERED_COUNT_KEY_IN_SESSION_STORAGE,n.toString()),window.sessionStorage.setItem(J.TRIGGERED_KEY_IN_SESSION_STORAGE,`true`),window.location.reload()}},n=(e,t)=>{let n=e.substring(10);if(e.startsWith(`context://`)&&(e=this.conf.contextRelativePath+n),t){let r=document.head.querySelector(`style[data-file-path='${e}']`);r||(r=document.createElement(`style`),r.setAttribute(`data-file-path`,e),document.head.appendChild(r),this.removeOldLinks(n)),r.textContent=t,document.dispatchEvent(new CustomEvent(`vaadin-theme-updated`))}else if(t===``||t===null){let t=document.head.querySelector(`style[data-file-path='${e}']`);t?t.remove():this.removeOldLinks(n),document.dispatchEvent(new CustomEvent(`vaadin-theme-updated`))}},r=this.getDedicatedWebSocketUrl();if(!r)return;let i=new I(r);i.onHandshake=()=>{J.isActive||i.setActive(!1),this.conf.usageStatisticsEnabled===!1?(localStorage.setItem(`vaadin.statistics.optout`,`true`),localStorage.removeItem(`vaadin.statistics.basket`),localStorage.removeItem(`vaadin.statistics.firstuse`)):localStorage.removeItem(`vaadin.statistics.optout`),this.elementTelemetry()},i.onConnectionError=e,i.onReload=t,i.onUpdate=n,i.onStatusChange=e=>{this.frontendStatus=e},i.onMessage=e=>this.handleFrontendMessage(e),this.frontendConnection=i,this.conf.backend===J.SPRING_BOOT_DEVTOOLS&&this.conf.liveReloadPort&&(this.javaConnection=new P(this.getSpringBootWebSocketUrl(window.location)),this.javaConnection.onHandshake=()=>{J.isActive||this.javaConnection.setActive(!1)},this.javaConnection.onReload=t,this.javaConnection.onConnectionError=e,this.javaConnection.onStatusChange=e=>{this.javaStatus=e})}removeOldLinks(e){Array.from(document.head.querySelectorAll(`link[rel="stylesheet"]`)).forEach(t=>{let n=t.getAttribute(`data-file-path`)||t.getAttribute(`href`);if(n){let r=n.split(/[?#]/)[0];(r===e||r.endsWith(`/`+e))&&t.remove()}})}tabHandleMessage(e,t){let n=e;return n.handleMessage&&n.handleMessage.call(e,t)}handleFrontendMessage(e){e.command===`featureFlags`||D(e,this.bodyShadowRoot)||this.handleHmrMessage(e)||this.unhandledMessages.push(e)}handleHmrMessage(e){return e.command===`hmr`?(X&&X.notifyListeners(e.data.event,e.data.eventData),!0):!1}getDedicatedWebSocketUrl(){function e(e){let t=document.createElement(`div`);return t.innerHTML=`<a href="${e}"/>`,t.firstChild.href}if(this.conf.url===void 0)return;let t=e(this.conf.url);if(!t.startsWith(`http://`)&&!t.startsWith(`https://`)){console.error(`The protocol of the url should be http or https for live reload to work.`);return}return`${t}?v-r=push&debug_window&token=${this.conf.token}`}getSpringBootWebSocketUrl(e){let{hostname:t}=e,n=e.protocol===`https:`?`wss`:`ws`;if(t.endsWith(`gitpod.io`)){let e=t.replace(/.*?-/,``);return`${n}://${this.conf.liveReloadPort}-${e}`}else return`${n}://${t}:${this.conf.liveReloadPort}`}connectedCallback(){super.connectedCallback(),this.bodyShadowRoot=document.body.attachShadow({mode:`closed`}),this.bodyShadowRoot.innerHTML=`<slot></slot>`,this.conf=window.Vaadin?.devToolsConf||this.conf,window.sessionStorage.getItem(J.TRIGGERED_KEY_IN_SESSION_STORAGE)&&window.sessionStorage.removeItem(J.TRIGGERED_KEY_IN_SESSION_STORAGE);let e=window.sessionStorage.getItem(`vaadin-hotswap-scroll`);e!==null&&(window.sessionStorage.removeItem(`vaadin-hotswap-scroll`),q(JSON.parse(e))),K();let t=window;t.Vaadin=t.Vaadin||{},t.Vaadin.devTools=Object.assign(this,t.Vaadin.devTools);let n=window.Vaadin;n.devToolsPlugins&&=(Array.from(n.devToolsPlugins).forEach(e=>this.initPlugin(e)),{push:e=>this.initPlugin(e)}),this.openWebSocketConnection(),j()}async initPlugin(e){let t=this;e.init({send:function(e,n){t.frontendConnection.send(e,n)}})}format(e){return e.toString()}checkLicense(e){this.frontendConnection?this.frontendConnection.send(`checkLicense`,e):w(e)}startPreTrial(){this.frontendConnection?this.frontendConnection.send(`startPreTrialLicense`,{}):(console.error(`Cannot start pre-trial: no connection`),d(!1,this.bodyShadowRoot))}downloadLicense(e,t){this.frontendConnection?this.frontendConnection.send(`downloadLicense`,{...e,...t}):f(`failed`,this.bodyShadowRoot)}setActive(e){this.frontendConnection?.setActive(e),this.javaConnection?.setActive(e),window.sessionStorage.setItem(J.ACTIVE_KEY_IN_SESSION_STORAGE,e?`true`:`false`)}render(){return n` <div style="display: none" class="dev-tools"></div>`}setJavaLiveReloadActive(e){this.javaConnection?this.javaConnection.setActive(e):this.frontendConnection?.setActive(e)}};o([i({type:String,attribute:!1})],Z.prototype,`frontendStatus`,void 0),o([i({type:String,attribute:!1})],Z.prototype,`javaStatus`,void 0),o([a()],Z.prototype,`componentPickActive`,void 0),Z=J=o([r(`vaadin-dev-tools`)],Z);
//# sourceMappingURL=vaadin-dev-tools.js.map