import { defineComponent, onBeforeUnmount, onMounted, provide, watch, inject } from 'vue';
import { useI18n } from 'vue-i18n';
import Ribbon from '@/core/ribbon/ribbon.vue';
import JhiFooter from '@/core/jhi-footer/jhi-footer.vue';
import JhiNavbar from '@/core/jhi-navbar/jhi-navbar.vue';
import LoginForm from '@/account/login-form/login-form.vue';

import '@/shared/config/dayjs';

import { useAccountStore } from './shared/config/store/account-store';
import { useNotificationStore } from './shared/config/store/notification-store';
import { useBvToast } from './shared/composables/bv-toast';

import sseNotificationService from './services/notification.service';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'App',
  components: {
    ribbon: Ribbon,
    'jhi-navbar': JhiNavbar,
    'login-form': LoginForm,
    'jhi-footer': JhiFooter,
  },
  setup() {
    const accountStore = useAccountStore();
    const notificationStore = useNotificationStore();
    const bvToast = useBvToast();
    const alertService = inject('alertService', () => useAlertService(), true);

    const manageSseConnection = () => {
      if (accountStore.authenticated) {
        const connectedUser = sseNotificationService.connect();
        if (connectedUser) {
          // fetch notifications
        }

        if (Notification.permission !== 'granted' && Notification.permission !== 'denied') {
          Notification.requestPermission();
        }
      } else {
        sseNotificationService.disconnect();
      }
    };

    watch(
      () => accountStore.authenticated,
      newAuthStatus => {
        console.info('App.vue: Auth status changed to: ', newAuthStatus);
        manageSseConnection();
      },
    );

    watch(
      () => [...notificationStore.sseNotifications],
      (newSseItems, oldSseItems) => {
        const latestItem = newSseItems.find(newItem => !oldSseItems.some(oldItem => oldItem.id === newItem.id));

        if (latestItem && latestItem.isSse) {
          const message = latestItem.message || 'Message';
          alertService.showInfo(message, {
            href: '/',
            title: `${latestItem.pqrsTitle}`,
          });
          /* bvToast.toast(latestItem.message || 'Notification', {
            title: latestItem.pqrsTitle,
            variant: 'info',
            solid: true,
            autoHideDelay: 7000,
            appendToast: true,
            toaster: 'b-toaster-top-right',
          }); */
        }
      },
      { deep: true },
    );

    onMounted(() => {
      manageSseConnection();
    });

    onBeforeUnmount(() => {
      sseNotificationService.cleanup();
    });

    provide('alertService', useAlertService());
    return {
      t$: useI18n().t,
    };
  },
});
