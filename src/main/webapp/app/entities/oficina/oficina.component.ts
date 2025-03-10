import { type Ref, type ComputedRef, defineComponent, inject, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';

import OficinaService from './oficina.service';
import { type IOficina } from '@/shared/model/oficina.model';
import { useAlertService } from '@/shared/alert/alert.service';
import { useAccountStore } from '@/shared/config/store/account-store';
import type LoginService from '@/account/login.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Oficina',
  setup() {
    const { t: t$ } = useI18n();
    const oficinaService = inject('oficinaService', () => new OficinaService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const oficinas: Ref<IOficina[]> = ref([]);

    const isFetching = ref(false);

    const loginService = inject<LoginService>('loginService');
    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const username = inject<ComputedRef<string>>('currentUsername');

    const esAdmin = ref(false);

    const clear = () => {};

    const retrieveOficinas = async () => {
      isFetching.value = true;
      try {
        const res = await oficinaService().retrieve();
        oficinas.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveOficinas();
    };

    onMounted(async () => {
      await retrieveOficinas();

      if (authenticated?.value) {
        const userRole = loginService?.getUserRole();
        console.log('🔍 Usuario autenticado con rol (onMounted):', userRole);
        //esAdmin.value = userRole === 'ROLE_ADMIN';
        if (userRole === 'ROLE_ADMIN') {
          esAdmin.value = true; //  Corrección aquí
          console.log(' esAdmin cambiado a TRUE');
        } else {
          esAdmin.value = false; // ✅ Corrección aquí
          console.log(' esAdmin cambiado a FALSE');
        }
      }
    });

    const removeId: Ref<string> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IOficina) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeOficina = async () => {
      try {
        await oficinaService().delete(removeId.value);
        const message = t$('ventanillaUnicaApp.oficina.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveOficinas();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      oficinas,
      handleSyncList,
      isFetching,
      retrieveOficinas,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeOficina,
      t$,
      authenticated, //** */
      username,
      esAdmin, // ** /
    };
  },
});
