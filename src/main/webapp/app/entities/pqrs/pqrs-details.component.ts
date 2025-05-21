import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import { StatesPqrs } from '@/constants';
import PqrsService from './pqrs.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { type IPqrs } from '@/shared/model/pqrs.model';
import { useAlertService } from '@/shared/alert/alert.service';

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

    const previousState = () => router.go(-1);
    const pqrs: Ref<IPqrs> = ref({});

    const retrievePqrs = async (pqrsId: string | string[]) => {
      try {
        const res = await pqrsService().find(pqrsId);
        pqrs.value = res;
      } catch (error: any) {
        alertService.showHttpError(error.response);
      }
    };

    const toggleEstadoPqrs = async () => {
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

    onMounted(async () => {
      const pqrsId: string | string[] = route.params.pqrsId;
      if (pqrsId) {
        await retrievePqrs(pqrsId);
      }
    });

    return {
      ...dateFormat,
      alertService,
      pqrs,
      StatesPqrs,
      ...dataUtils,

      previousState,
      t$: useI18n().t,
      t,
      formatDateLong,
      toggleEstadoPqrs,
    };
  },
});
