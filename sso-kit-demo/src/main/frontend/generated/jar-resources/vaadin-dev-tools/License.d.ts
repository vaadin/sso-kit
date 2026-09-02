import { ServerMessage } from './vaadin-dev-tools';
export interface Product {
    name: string;
    version: string;
}
export interface DownloadLicenseOptions {
    timeout?: number;
}
export interface PreTrial {
    trialName?: String;
    trialState: String;
    daysRemaining?: number;
    daysRemainingUntilRenewal?: number;
}
export interface ProductAndMessage {
    message: string;
    messageHtml?: string;
    product: Product;
    preTrial?: PreTrial;
}
/**
 * Name of the event fired on `document` when a license has been successfully
 * downloaded (e.g. after acquiring a trial or subscription from DevTools).
 *
 * The event is cancelable and carries the affected {@link Product} in
 * `event.detail`. By default, DevTools reloads the page when the license is
 * downloaded. This is necessary because components that failed the license
 * check are replaced in the DOM by `<no-license>` placeholders (see
 * {@link showNoLicenseFallback}); a refresh/reload is required to recreate the
 * real components so that they show up again.
 *
 * Consumers that are able to recover without a full reload (for example by
 * resuming an ongoing operation themselves) can listen for this event and call
 * `event.preventDefault()` to suppress the default reload.
 */
export declare const LICENSE_DOWNLOAD_COMPLETED_EVENT = "vaadin-license-download-completed";
export declare const findAll: (element: Element | ShadowRoot | Document, tags: string[]) => Element[];
export declare const licenseCheckOk: (data: Product) => void;
export declare const licenseCheckFailed: (data: ProductAndMessage) => void;
export declare const licenseCheckNoKey: (data: ProductAndMessage) => void;
export declare const handleLicenseMessage: (message: ServerMessage, bodyShadowRoot: ShadowRoot | null) => boolean;
export declare const startPreTrial: () => void;
export declare const tryAcquireLicense: (options?: DownloadLicenseOptions) => void;
export declare const licenseInit: () => void;
