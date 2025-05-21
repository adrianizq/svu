import { defineStore } from 'pinia';
import axios from 'axios';

const baseApiUrl = 'api/notification';

export interface NotificationItem {
  id?: number | string;
  type?: string;
  currentDate?: Date | string;
  message?: string | undefined;
  read?: boolean;
  userLogin?: string;
  pqrsId?: string;
  pqrsTitle?: string;
  pqrsResponseDueDate?: string;
  isSse?: boolean;
}

export interface NotificationState {
  sseNotifications: NotificationItem[];
  persistentNotifications: NotificationItem[];
  isLoading: boolean;
  error: string | null;
}

export const useNotificationStore = defineStore('notification', {
  state: (): NotificationState => ({
    sseNotifications: [],
    persistentNotifications: [],
    isLoading: false,
    error: null,
  }),
  getters: {
    allDisplayNotifications(state): NotificationItem[] {
      const combined = [
        ...state.persistentNotifications,
        ...state.sseNotifications.filter(sse => !state.persistentNotifications.find(p => p.pqrsId === sse.pqrsId && !p.isSse)),
      ];
      return combined.sort((a, b) => new Date(b.currentDate || 0).getTime() - new Date(a.currentDate || 0).getTime());
    },
    unreadCount(state): number {
      return state.persistentNotifications.filter(n => !n.read).length;
    },
  },
  actions: {
    // Add temporary ... why?
    addSseEvent(eventData: any) {
      const sseNotificationItem: NotificationItem = {
        id: `sse-${Date.now()}-${Math.random()}`,
        pqrsId: eventData.id,
        type: eventData.type || 'PQRS_DUE_DATE_REMINDER',
        message: eventData.message || `PQRS '${eventData.title}' is due soon.`,
        pqrsTitle: eventData.title,
        pqrsResponseDueDate: eventData.responseDueDate,
        currentDate: new Date(),
        read: false,
        isSse: true,
      };
      this.sseNotifications.unshift(sseNotificationItem);

      if (this.sseNotifications.length > 10) {
        this.sseNotifications.pop();
      }
      return sseNotificationItem;
    },

    async fetchUserNotifications() {
      if (this.isLoading) return;
      this.isLoading = true;
      this.error = null;
      try {
        const response = await axios.get<NotificationItem[]>(`${baseApiUrl}/me?sort=fecha,desc&leido.equals=false`);
        this.persistentNotifications = response.data.map(n => ({ ...n, isSse: false }));
      } catch (err: any) {
        this.error = err.message || 'Failed to fetch notifications';
        console.error('Error fetching notifications', err);
      } finally {
        this.isLoading = false;
      }
    },
  },
});
