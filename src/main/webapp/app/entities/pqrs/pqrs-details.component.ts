import { type Ref, computed, defineComponent, inject, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import { StatesPqrs } from '@/constants';
import PqrsService from './pqrs.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { type IPqrs } from '@/shared/model/pqrs.model';
import { useAlertService } from '@/shared/alert/alert.service';
import { useAccountStore } from '@/shared/config/store/account-store';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PqrsDetails',
  setup() {
    const { t } = useI18n();
    const dateFormat = useDateFormat();
    const pqrsService = inject('pqrsService', () => new PqrsService());
    const alertService = inject('alertService', () => useAlertService(), true);
    const { formatDateLong } = useDateFormat();

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const accountStore = useAccountStore();

    const previousState = () => router.go(-1);
    const pqrs: Ref<IPqrs> = ref({});

    const isConfirmCloseModalVisible = ref(false);
    const confirmCloseModalRef = ref(null);

    const isFuncionario = computed(() => {
      return accountStore.account?.authorities?.includes('ROLE_FUNCTIONARY') ?? false;
    });

    const isAdmin = computed(() => {
      return accountStore.account?.authorities?.includes('ROLE_ADMIN') ?? false;
    });

    const retrievePqrs = async (pqrsId: string | string[]) => {
      try {
        const res = await pqrsService().find(pqrsId);
        pqrs.value = res;
      } catch (error: any) {
        alertService.showHttpError(error.response);
      }
    };

    const toggleEstadoPqrs = async () => {
      if (!isFuncionario.value) {
        console.warn('Intento de cambiar estado sin ser funcionario.');
        return;
      }

      if (pqrs.value && pqrs.value.id) {
        let newState: string;
        let successMessageKey: string;

        if (pqrs.value.estado === StatesPqrs.Resolved) {
          newState = StatesPqrs.InProcess;
          successMessageKey = 'ventanillaUnicaApp.pqrs.messages.inProgresSuccess';
        } else {
          newState = StatesPqrs.Resolved;
          successMessageKey = 'ventanillaUnicaApp.pqrs.messages.resolvedSuccess';
        }

        const pqrsToUpdate: IPqrs = {
          ...pqrs.value,
          estado: newState,
        };

        try {
          const result = await pqrsService().update(pqrsToUpdate);
          pqrs.value = result;
          alertService.showSuccess(t(successMessageKey));
        } catch (error: any) {
          console.error('Error al cambiar estado de PQRS:', error);
          const errorMessageKey = 'ventanillaUnicaApp.pqrs.messages.resolvedSuccess';
          if (error.response) {
            alertService.showHttpError(error.response);
          } else {
            alertService.showError(t('ventanillaUnicaApp.pqrs.messages.resolvedError'));
          }
        }
      }
    };

    const openConfirmCloseModal = () => {
      if (!isAdmin.value) {
        return;
      }
      isConfirmCloseModalVisible.value = true;
    };

    const handleConfirmClose = async (bvModalEvent: Event) => {
      await confirmClosePqrs();
    };

    const confirmClosePqrs = async () => {
      if (pqrs.value && pqrs.value.id) {
        const pqrsToUpdate: IPqrs = {
          ...pqrs.value,
          estado: StatesPqrs.Closed,
        };

        try {
          const result = await pqrsService().update(pqrsToUpdate);
          pqrs.value = result;
          alertService.showSuccess(t('ventanillaUnicaApp.pqrs.messages.closedSuccess'));
          isConfirmCloseModalVisible.value = false;
        } catch (error) {
          console.error('Error al cerrar PQRS:', error);
          const errorMessageKey = 'ventanillaUnicaApp.pqrs.messages.closeError';
          if (error.response) {
            alertService.showHttpError(error.response);
          } else {
            alertService.showError(t(errorMessageKey));
          }
        }
      }
    };

    onMounted(async () => {
      const pqrsId: string | string[] = route.params.pqrsId;
      if (pqrsId) {
        await retrievePqrs(pqrsId);
      } else {
        console.error('PQRS ID no encontrado en los parámetros de la ruta.');
        alertService.showError(t('ventanillaUnicaApp.pqrs.messages.notFound'));
        previousState();
      }
    });

    return {
      ...dateFormat,
      alertService,
      pqrs,
      StatesPqrs,
      isConfirmCloseModalVisible,
      confirmCloseModalRef,
      isFuncionario,
      isAdmin,
      openConfirmCloseModal,
      handleConfirmClose,
      ...dataUtils,

      previousState,
      t$: useI18n().t,
      t,
      formatDateLong,
      toggleEstadoPqrs,
    };
  },
});
