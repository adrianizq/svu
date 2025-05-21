// src/services/notification.service.ts
import type { EventSourceMessage } from '@microsoft/fetch-event-source';
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { useAccountStore } from '@/shared/config/store/account-store';
import { useNotificationStore } from '@/shared/config/store/notification-store';

class SseNotificationService {
  private ctrl: AbortController | null = null;
  private pageVisibilityHandler: (() => void) | null = null;
  private isPageVisible: boolean = !document.hidden;

  constructor() {
    this.setupPageVisibilityListener();
  }

  private setupPageVisibilityListener(): void {
    this.pageVisibilityHandler = () => {
      this.isPageVisible = !document.hidden;
    };
    document.addEventListener('visibilitychange', this.pageVisibilityHandler, false);
  }

  private shouldShowNativeNotification(): boolean {
    return !this.isPageVisible;
  }

  public connect(): string | null {
    const accountStore = useAccountStore();

    const token = localStorage.getItem('jhi-authenticationToken') || sessionStorage.getItem('jhi-authenticationToken');

    if (!accountStore.authenticated || !token || !accountStore.account?.login) {
      console.log('User not authenticated, token missing, or login not available. SSE connection not started.');
      this.disconnect(); // Ensure any previous controller is aborted
      return null;
    }
    const userLogin = accountStore.account.login;

    if (this.ctrl && !this.ctrl.signal.aborted) {
      console.log('SSE connection process already active or controller not aborted for', userLogin);
      this.disconnect();
      return userLogin;
    }

    console.log('Attempting to connect to SSE for user:', userLogin);
    this.ctrl = new AbortController();
    const notificationStore = useNotificationStore();

    fetchEventSource('/api/sse-notifications/subscribe', {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
        Accept: 'text/event-stream',
      },
      signal: this.ctrl.signal,

      onopen: async (response: any) => {
        if (response.ok && response.headers.get('content-type') === 'text/event-stream') {
          console.log('SSE connection opened successfully by fetchEventSource for user:', userLogin);
          return;
        } else if (response.status >= 400 && response.status < 500 && response.status !== 429) {
          console.error(`SSE connection failed (client error): ${response.status} ${response.statusText}`);

          const bodyText = await response.text();
          console.error('Error body:', bodyText);
          notificationStore.error = `SSE Connection Failed: ${response.status}`;
          throw new Error(`Client error ${response.status}`);
        } else {
          console.warn(`SSE connection issue (will retry): ${response.status} ${response.statusText}`);
          notificationStore.error = `SSE Connection Issue: ${response.status}`;
        }
      },
      onmessage: (event: EventSourceMessage) => {
        console.log('SSE message received:', event);
        if (event.event === 'PQRS_DUE_DATE_REMINDER') {
          console.log('Received pqrsDueDateReminder (raw):', event.data);
          try {
            const notificationData = JSON.parse(event.data);
            const sseNotif = notificationStore.addSseEvent(notificationData);
            //const uri = new URL('/content/images/logo-jhipster.png');
            //console.log('MY URI: ', uri);
            const iconURL = 'https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png';

            if (this.shouldShowNativeNotification()) {
              if (Notification.permission === 'granted') {
                new Notification('PQRS Due Date Reminder', {
                  icon: iconURL,
                  body: sseNotif.message || `PQRS '${sseNotif.pqrsTitle}' is approaching its due date.`,
                });
              }
            }
          } catch (e) {
            console.error('Error parsing or handling pqrsDueDateReminder event data:', e);
          }
        } else if (event.event === 'connected') {
          console.log('SSE server sent connected event:', event.data);
        } else if (event.data) {
          console.log('Received generic SSE message (fetchEventSource):', event.data);
        }
      },
      onclose: () => {
        console.log('SSE connection closed by fetchEventSource.');
      },
      onerror: (err: any) => {
        console.error('SSE connection error (fetchEventSource):', err);
        notificationStore.error = `SSE Error: ${err.message || 'Unknown error'}`;

        if (this.ctrl?.signal.aborted) {
          console.log('SSE error due to abort, stopping retries.');
          throw err;
        }
      },
      openWhenHidden: true,
    }).catch(err => {
      if (err.name !== 'AbortError') {
        console.error('fetchEventSource fatal error, retries stopped:', err);
        notificationStore.error = `SSE Fatal Error: ${err.message}`;
      }
    });

    return userLogin;
  }

  public disconnect(): void {
    if (this.ctrl) {
      console.log('Aborting SSE connection.');
      this.ctrl.abort();
      this.ctrl = null;
    }
  }

  public cleanup(): void {
    this.disconnect();
    if (this.pageVisibilityHandler) {
      document.removeEventListener('visibilitychange', this.pageVisibilityHandler);
      this.pageVisibilityHandler = null;
    }
  }
}

const sseNotificationService = new SseNotificationService();
export default sseNotificationService;
