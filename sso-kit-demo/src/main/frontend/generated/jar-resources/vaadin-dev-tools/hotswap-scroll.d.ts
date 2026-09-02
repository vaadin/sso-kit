export type ScrollSnapshot = Record<string, {
    scrollTop: number;
    scrollLeft: number;
}>;
/**
 * Captures scroll positions of the window and all scrolled elements.
 * Elements are keyed by CSS selector path so they can be found after DOM rebuild.
 */
export declare function captureScrollPositions(): ScrollSnapshot;
/**
 * Captures scroll positions, sends a ui-refresh event to all Flow clients,
 * and restores scroll positions once the clients are idle.
 * Used by both the Push-based (vaadin-refresh-ui event) and WebSocket-based
 * hot-swap paths.
 */
export declare function refreshWithScrollPreservation(fullRefresh: boolean): void;
/**
 * Registers a window event listener for 'vaadin-refresh-ui' that triggers
 * a scroll-preserving UI refresh. Guards against double-registration.
 */
export declare function registerRefreshUIHandler(): void;
/**
 * Restores scroll positions after a hot-swap UI refresh completes.
 * Polls Flow client isActive() to wait until UIDL processing is done,
 * then uses requestAnimationFrame to restore all captured scroll positions.
 */
export declare function restoreScrollPositions(snapshot: ScrollSnapshot): void;
